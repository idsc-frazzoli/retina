// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.math.UniformBSpline2;
import ch.ethz.idsc.sophus.planar.Cross2D;
import ch.ethz.idsc.sophus.planar.Det2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Power;

// TODO JPH/MH need estimation of length of track so that resolution can be adapted
public final class BSplineTrack implements TrackInterface {
  private static final int SPLINE_ORDER = 2;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  private static final int LOOKUP_SKIP = 200;
  static final float LOOKUP_RES = 1f / LOOKUP_SKIP;
  // ---
  /** matrix of dimension n x 3 */
  private final Tensor points_xyr;
  /** matrix of dimension n x 2 */
  private final Tensor points_xy;
  /** vector of length n */
  private final Tensor points_r;
  private final boolean closed;
  private final int numPoints;
  private final int effPoints;
  // for fast lookup using floats
  private final float[] posX;
  private final float[] posY;

  /** @param points_xyr matrix with dimension n x 3
   * * @param closed */
  public BSplineTrack(Tensor points_xyr, boolean closed) {
    this.points_xyr = points_xyr;
    this.closed = closed;
    numPoints = points_xyr.length();
    points_xy = Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION));
    points_r = points_xyr.get(Tensor.ALL, 2);
    effPoints = numPoints + (closed ? 0 : -1);
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

  public Tensor getControlPoints() {
    return points_xy.copy();
  }

  public Tensor combinedControlPoints() {
    return points_xyr;
  }

  public int numPoints() {
    return numPoints;
  }

  /** get position at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return position [m] */
  public Tensor getPositionXY(Scalar pathProgress) {
    return UniformBSpline2.getBasisVector(numPoints, 0, closed, pathProgress).dot(points_xy);
  }

  /** get the path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1] */
  public Tensor getDerivationXY(Scalar pathProgress) {
    return UniformBSpline2.getBasisVector(numPoints, 1, closed, pathProgress).dot(points_xy);
  }

  /** get radius at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return radius [m] */
  public Scalar getRadius(Scalar pathProgress) {
    return UniformBSpline2.getBasisVector(numPoints, 0, closed, pathProgress).dot(points_r).Get();
  }

  /** get the path direction with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public Tensor getDirectionXY(Scalar pathProgress) {
    return NORMALIZE.apply(getDerivationXY(pathProgress));
  }

  /** get perpendicular vector to the right of the path
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  Tensor getLeftDirectionXY(Scalar pathProgress) {
    return Cross2D.of(getDirectionXY(pathProgress));
  }

  /** get the 2nd path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1^2] */
  public Tensor get2ndDerivation(Scalar pathProgress) {
    return UniformBSpline2.getBasisVector(numPoints, 2, closed, pathProgress).dot(points_xy);
  }

  /** get the curvature
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return curvature unit [1/m] */
  public Scalar getSignedCurvature(Scalar pathProgress) {
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
  public Scalar getNearestPathProgress(Tensor position) {
    if (closed)
      return getFastNearestPathProgress(position);
    return getFastNearestPathProgressOpen(position);
  }

  /** problem: using normal BSpline implementation takes more time than full MPC optimization
   * solution: fast position lookup: from 45000 micro s -> 15 micro s
   * @param position vector */
  private Scalar getFastNearestPathProgress(Tensor position) {
    float gPosX = position.Get(0).number().floatValue();
    float gPosY = position.Get(1).number().floatValue();
    // first control point
    float bestDist = 10000f;
    int bestGuess = 0;
    // initial guesses
    for (int i = 0; i < numPoints; i++) {
      int index = i * LOOKUP_SKIP;
      // quadratic distances
      float dist = getFastQuadraticDistance(index, gPosX, gPosY);
      if (dist < bestDist) {
        bestDist = dist;
        bestGuess = index;
      }
    }
    // refinement
    int precision = 4;
    while (precision > 1) {
      int upper = bestGuess + precision;
      if (upper >= posX.length)
        upper -= posX.length;
      if (upper < 0)
        upper += posX.length;
      float upperDist = getFastQuadraticDistance(upper, gPosX, gPosY);
      int lower = bestGuess - precision;
      if (lower >= posX.length)
        lower -= posX.length;
      if (lower < 0)
        lower += posX.length;
      float lowerDist = getFastQuadraticDistance(lower, gPosX, gPosY);
      if (lowerDist < bestDist) {
        bestGuess = lower;
        bestDist = lowerDist;
      } else //
      if (upperDist < bestDist) {
        bestGuess = upper;
        bestDist = upperDist;
      } else
        precision /= 2;
      // System.out.println("pos: "+bestGuess*lookupRes);
      // System.out.println(Math.sqrt(getFastQuadraticDistance(bestGuess, gPosX, gPosY)));
    }
    return RealScalar.of(bestGuess * LOOKUP_RES);
  }

  /** problem: using normal BSpline implementation takes more time than full MPC optimization
   * solution: fast position lookup: from 45000 micro s -> 15 micro s */
  private Scalar getFastNearestPathProgressOpen(Tensor position) {
    float gPosX = position.Get(0).number().floatValue();
    float gPosY = position.Get(1).number().floatValue();
    // first control point
    float bestDist = 10000f;
    int bestGuess = 0;
    // initial guesses
    for (int i = 0; i < numPoints - SPLINE_ORDER; ++i) {
      int index = i * LOOKUP_SKIP;
      // quadratic distances
      float dist = getFastQuadraticDistance(index, gPosX, gPosY);
      if (dist < bestDist) {
        bestDist = dist;
        bestGuess = index;
      }
    }
    // refinement
    int precision = 4;
    while (precision > 1) {
      int upper = bestGuess + precision;
      if (upper >= posX.length)
        upper = posX.length - 1;
      if (upper < 0)
        upper = 0;
      float upperDist = getFastQuadraticDistance(upper, gPosX, gPosY);
      int lower = bestGuess - precision;
      if (lower >= posX.length)
        lower = posX.length - 1;
      if (lower < 0)
        lower = 0;
      float lowerDist = getFastQuadraticDistance(lower, gPosX, gPosY);
      if (lowerDist < bestDist) {
        bestGuess = lower;
        bestDist = lowerDist;
      } else //
      if (upperDist < bestDist) {
        bestGuess = upper;
        bestDist = upperDist;
      } else
        precision /= 2;
      // System.out.println("pos: "+bestGuess*lookupRes);
      // System.out.println(Math.sqrt(getFastQuadraticDistance(bestGuess, gPosX, gPosY)));
    }
    return RealScalar.of(bestGuess * LOOKUP_RES);
  }

  float getFastQuadraticDistance(int index, float gPosX, float gPosY) {
    float dx = gPosX - posX[index];
    float dy = gPosY - posY[index];
    // quadratic distances
    return dx * dx + dy * dy;
  }

  private Scalar getDist(Tensor from, Scalar pathProgress) {
    return Norm._2.of(getPositionXY(pathProgress).subtract(from));
  }

  @Override // from TrackInterface
  public boolean isInTrack(Tensor position) {
    Scalar prog = getNearestPathProgress(position);
    Scalar dist = getDist(position, prog);
    return Scalars.lessThan(dist, getRadius(prog));
  }

  @Override // from TrackInterface
  public Tensor getNearestPosition(Tensor position) {
    return getPositionXY(getNearestPathProgress(position));
  }

  @Override // from TrackInterface
  public Tensor getLineMiddle(int resolution) {
    return Range.of(0, resolution).multiply(RealScalar.of(effPoints / (double) resolution)) //
        .map(this::getPositionXY);
  }

  @Override // from TrackInterface
  public Tensor getLineLeft(int resolution) {
    // this is not accurate for large changes in radius
    return Range.of(0, resolution).multiply(RealScalar.of(effPoints / (double) resolution)) //
        .map(prog -> getPositionXY(prog).subtract(getLeftDirectionXY(prog).multiply(getRadius(prog))));
  }

  @Override // from TrackInterface
  public Tensor getLineRight(int resolution) {
    // this is not accurate for large changes in radius
    return Range.of(0, resolution).multiply(RealScalar.of(effPoints / (double) resolution)) //
        .map(prog -> getPositionXY(prog).add(getLeftDirectionXY(prog).multiply(getRadius(prog))));
  }
}
