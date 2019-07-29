// code by mh
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.retina.util.spline.BSpline2Vector;
import ch.ethz.idsc.sophus.math.Det2D;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Power;

// TODO JPH/MH need estimation of length of track so that resolution can be adapted
public abstract class BSplineTrack implements TrackInterface {
  protected static final int SPLINE_ORDER = 2;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  protected static final int LOOKUP_SKIP = 200;
  static final float LOOKUP_RES = 1f / LOOKUP_SKIP;
  // ---
  /** matrix of dimension n x 3 */
  private final Tensor points_xyr;
  /** matrix of dimension n x 2 */
  private final Tensor points_xy;
  /** vector of length n */
  private final Tensor points_r;
  protected final int numPoints;
  private final ScalarTensorFunction bSpline2VectorD0;
  private final ScalarTensorFunction bSpline2VectorD1;
  private final ScalarTensorFunction bSpline2VectorD2;
  private final int effPoints;
  // for fast lookup using floats
  protected final float[] posX;
  protected final float[] posY;

  /** @param points_xyr matrix with dimension n x 3
   * * @param cyclic */
  public BSplineTrack(Tensor points_xyr, boolean cyclic) {
    this.points_xyr = points_xyr;
    numPoints = points_xyr.length();
    bSpline2VectorD0 = BSpline2Vector.of(numPoints, 0, cyclic);
    bSpline2VectorD1 = BSpline2Vector.of(numPoints, 1, cyclic);
    bSpline2VectorD2 = BSpline2Vector.of(numPoints, 2, cyclic);
    points_xy = Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION));
    points_r = points_xyr.get(Tensor.ALL, 2);
    effPoints = numPoints + (cyclic ? 0 : -2);
    // prepare lookup
    posX = new float[(int) (effPoints / LOOKUP_RES)];
    posY = new float[(int) (effPoints / LOOKUP_RES)];
    for (int i = 0; i < posX.length; ++i) {
      Tensor pos = getPositionXY(RealScalar.of(i * LOOKUP_RES));
      posX[i] = pos.Get(0).number().floatValue();
      posY[i] = pos.Get(1).number().floatValue();
    }
  }

  public final Tensor combinedControlPoints() {
    return points_xyr;
  }

  public final int numPoints() {
    return numPoints;
  }

  /** get position at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return position [m] */
  public final Tensor getPositionXY(Scalar pathProgress) {
    return bSpline2VectorD0.apply(pathProgress).dot(points_xy);
  }

  /** get radius at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return radius [m] */
  public final Scalar getRadius(Scalar pathProgress) {
    return bSpline2VectorD0.apply(pathProgress).dot(points_r).Get();
  }

  /** get the path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1] */
  public final Tensor getDerivationXY(Scalar pathProgress) {
    return bSpline2VectorD1.apply(pathProgress).dot(points_xy);
  }

  /** get the path direction with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public final Tensor getDirectionXY(Scalar pathProgress) {
    return NORMALIZE.apply(getDerivationXY(pathProgress));
  }

  /** get perpendicular vector to the right of the path
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  final Tensor getLeftDirectionXY(Scalar pathProgress) {
    return Cross.of(getDirectionXY(pathProgress));
  }

  /** get the 2nd path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1^2] */
  public final Tensor get2ndDerivation(Scalar pathProgress) {
    return bSpline2VectorD2.apply(pathProgress).dot(points_xy);
  }

  /** get the curvature
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return curvature unit [1/m] */
  public final Scalar getSignedCurvature(Scalar pathProgress) {
    Tensor firstDer = getDerivationXY(pathProgress);
    Tensor secondDer = get2ndDerivation(pathProgress);
    Scalar under = Power.of(Norm._2.of(firstDer), 3.0);
    return Det2D.of(firstDer, secondDer).divide(under);
  }

  // function local radius is not used/tested and numerically unstable
  // public Scalar getLocalRadius(Scalar pathProgress) {
  // the application of abs() causes a loss of information
  // return getCurvature(pathProgress).abs().reciprocal();
  // }
  /** problem: using normal BSpline implementation takes more time than full MPC optimization
   * solution: fast position lookup: from 45000 micro s -> 15 micro s
   * 
   * @param position vector */
  public abstract Scalar getNearestPathProgress(Tensor position);

  final float getFastQuadraticDistance(int index, float gPosX, float gPosY) {
    float dx = gPosX - posX[index];
    float dy = gPosY - posY[index];
    // quadratic distances
    return dx * dx + dy * dy;
  }

  @Override // from TrackInterface
  public final boolean isInTrack(Tensor position) {
    Scalar pathProgress = getNearestPathProgress(position);
    return Scalars.lessThan( //
        Norm._2.between(position, getPositionXY(pathProgress)), //
        getRadius(pathProgress));
  }

  @Override // from TrackInterface
  public final Tensor getNearestPosition(Tensor position) {
    return getPositionXY(getNearestPathProgress(position));
  }

  private Tensor domain(int resolution) {
    return Range.of(0, resolution).multiply(RealScalar.of(effPoints / (double) resolution));
  }

  @Override // from TrackInterface
  public final Tensor getLineMiddle(int resolution) {
    return domain(resolution).map(this::getPositionXY);
  }

  @Override // from TrackInterface
  public final TrackBoundaries getTrackBoundaries(int resolution) {
    // this is not accurate for large changes in radius
    Tensor domain = domain(resolution);
    Tensor middle = domain.map(this::getPositionXY);
    Tensor normal = domain.map(prog -> getLeftDirectionXY(prog).multiply(getRadius(prog)));
    Tensor lineL = middle.add(normal);
    Tensor lineR = middle.subtract(normal);
    return new SampledTrackBoundaries(middle, lineL, lineR);
  }
}
