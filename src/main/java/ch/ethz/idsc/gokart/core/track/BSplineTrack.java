// code by mh
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.retina.util.math.UniformBSpline2;
import ch.ethz.idsc.sophus.math.Det2D;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.lie.Cross;
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
  private final boolean closed;
  protected final int numPoints;
  private final int effPoints;
  // for fast lookup using floats
  protected final float[] posX;
  protected final float[] posY;

  /** @param points_xyr matrix with dimension n x 3
   * * @param closed */
  public BSplineTrack(Tensor points_xyr, boolean closed) {
    this.points_xyr = points_xyr;
    this.closed = closed;
    numPoints = points_xyr.length();
    points_xy = Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION));
    points_r = points_xyr.get(Tensor.ALL, 2);
    effPoints = numPoints + (closed ? 0 : -2);
    // prepare lookup
    posX = new float[(int) (effPoints / LOOKUP_RES)];
    posY = new float[(int) (effPoints / LOOKUP_RES)];
    for (int i = 0; i < posX.length; ++i) {
      Tensor pos = getPositionXY(RealScalar.of(i * LOOKUP_RES));
      posX[i] = pos.Get(0).number().floatValue();
      posY[i] = pos.Get(1).number().floatValue();
    }
  }

  @Override // from TrackInterface
  public boolean isClosed() {
    return closed;
  }

  public final Tensor getControlPoints() {
    return points_xy.copy();
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
    return UniformBSpline2.getBasisVector(numPoints, 0, closed, pathProgress).dot(points_xy);
  }

  /** get the path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1] */
  public final Tensor getDerivationXY(Scalar pathProgress) {
    return UniformBSpline2.getBasisVector(numPoints, 1, closed, pathProgress).dot(points_xy);
  }

  /** get radius at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return radius [m] */
  public final Scalar getRadius(Scalar pathProgress) {
    return UniformBSpline2.getBasisVector(numPoints, 0, closed, pathProgress).dot(points_r).Get();
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
    return UniformBSpline2.getBasisVector(numPoints, 2, closed, pathProgress).dot(points_xy);
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
  public abstract Scalar getNearestPathProgress(Tensor position);

  final float getFastQuadraticDistance(int index, float gPosX, float gPosY) {
    float dx = gPosX - posX[index];
    float dy = gPosY - posY[index];
    // quadratic distances
    return dx * dx + dy * dy;
  }

  private Scalar getDist(Tensor from, Scalar pathProgress) {
    return Norm._2.of(getPositionXY(pathProgress).subtract(from));
  }

  @Override // from TrackInterface
  public final boolean isInTrack(Tensor position) {
    Scalar prog = getNearestPathProgress(position);
    Scalar dist = getDist(position, prog);
    return Scalars.lessThan(dist, getRadius(prog));
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
