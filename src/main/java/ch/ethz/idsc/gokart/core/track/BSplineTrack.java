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

// TODO JPH need estimation of length of track so that resolution can be adapted
public abstract class BSplineTrack implements TrackInterface {
  private static final int SPLINE_ORDER = 2;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  // ---
  static final int LOOKUP_SKIP = 200;
  static final float LOOKUP_RES = 1f / LOOKUP_SKIP;

  /** @param points_xyr of dimensions n x 3 with scalars of same unit
   * @param cyclic
   * @return
   * @throws Exception if given points_xyr is null */
  public static BSplineTrack of(Tensor points_xyr, boolean cyclic) {
    return cyclic //
        ? new BSplineTrackCyclic(points_xyr)
        : new BSplineTrackString(points_xyr);
  }

  // ---
  /** matrix of dimension n x 3 */
  private final Tensor points_xyr;
  /** matrix of dimension n x 2 */
  private final Tensor points_xy;
  /** vector of length n */
  private final Tensor points_r;
  private final int numPoints;
  private final boolean cyclic;
  private final ScalarTensorFunction bSpline2VectorD0;
  private final ScalarTensorFunction bSpline2VectorD1;
  private final ScalarTensorFunction bSpline2VectorD2;
  private final int effPoints;
  // for fast lookup using floats
  protected final float[] posX;
  protected final float[] posY;

  /** @param points_xyr matrix with dimension n x 3
   * * @param cyclic */
  protected BSplineTrack(Tensor points_xyr, boolean cyclic) {
    this.points_xyr = points_xyr;
    numPoints = points_xyr.length();
    this.cyclic = cyclic;
    bSpline2VectorD0 = BSpline2Vector.of(numPoints, 0, cyclic);
    bSpline2VectorD1 = BSpline2Vector.of(numPoints, 1, cyclic);
    bSpline2VectorD2 = BSpline2Vector.of(numPoints, 2, cyclic);
    points_xy = Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION));
    points_r = points_xyr.get(Tensor.ALL, 2);
    effPoints = numPoints + (cyclic ? 0 : -SPLINE_ORDER);
    // prepare lookup
    posX = new float[(int) (effPoints / LOOKUP_RES)];
    posY = new float[(int) (effPoints / LOOKUP_RES)];
    for (int i = 0; i < posX.length; ++i) {
      Tensor pos = getPositionXY(RealScalar.of(i * LOOKUP_RES));
      posX[i] = pos.Get(0).number().floatValue();
      posY[i] = pos.Get(1).number().floatValue();
    }
  }

  /** @return matrix with rows of the form {px[m], py[m], radius[m]} */
  public final Tensor combinedControlPoints() {
    return points_xyr;
  }

  /** @return length of control points */
  public final int numPoints() {
    return numPoints;
  }

  /** @return length of control points minus spline order if non-cyclic */
  public final int effPoints() {
    return effPoints;
  }

  @Override // from TrackInterface
  public final boolean isClosed() {
    return cyclic;
  }

  /** @param pathProgress along center line
   * @return position along center line {px[m], py[m]} */
  public final Tensor getPositionXY(Scalar pathProgress) {
    return bSpline2VectorD0.apply(pathProgress).dot(points_xy);
  }

  /** @param pathProgress along center line
   * @return radius [m] */
  public final Scalar getRadius(Scalar pathProgress) {
    // TODO JPH attempt to replace by de boors algo
    // return BSplineFunction.of(2, points_r).apply(pathProgress).Get();
    return bSpline2VectorD0.apply(pathProgress).dot(points_r).Get();
  }

  /** get the path derivative with respect to path progress
   * 
   * @param pathProgress along center line corresponding to control point indices
   * @return change rate of position unit [m/1] */
  public final Tensor getDerivationXY(Scalar pathProgress) {
    return bSpline2VectorD1.apply(pathProgress).dot(points_xy);
  }

  /** get the path direction with respect to path progress
   * 
   * @param pathProgress along center line
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public final Tensor getDirectionXY(Scalar pathProgress) {
    return NORMALIZE.apply(getDerivationXY(pathProgress));
  }

  /** get perpendicular vector to the right of the path
   * 
   * @param pathProgress along center line
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  /* package */ final Tensor getLeftDirectionXY(Scalar pathProgress) {
    return Cross.of(getDirectionXY(pathProgress));
  }

  /** get the 2nd path derivative with respect to path progress
   * 
   * @param pathProgress along center line
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1^2] */
  public final Tensor get2ndDerivation(Scalar pathProgress) {
    return bSpline2VectorD2.apply(pathProgress).dot(points_xy);
  }

  /** get the curvature
   * 
   * @param pathProgress along center line
   * corresponding to control point indices [1]
   * @return curvature unit [m^-1] */
  public final Scalar getSignedCurvature(Scalar pathProgress) {
    Tensor firstDer = getDerivationXY(pathProgress);
    Tensor secondDer = get2ndDerivation(pathProgress);
    Scalar under = Power.of(Norm._2.of(firstDer), 3.0);
    return Det2D.of(firstDer, secondDer).divide(under);
  }

  /** path progress does not depend on radius
   * 
   * problem: using normal BSpline implementation takes more time than full MPC optimization
   * solution: fast position lookup: from 45000 micro s -> 15 micro s
   * 
   * @param position of the form {px[m], py[m]}
   * @return parameter of center line curve */
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
  public final TrackLane getTrackBoundaries(int resolution) {
    return new TrackLane(this, domain(resolution));
  }
}
