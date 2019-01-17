// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.math.UniformBSpline2;
import ch.ethz.idsc.sophus.planar.Det2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Power;

public class BSplineTrack implements TrackInterface {
  private static final int SPLINE_ORDER = 2;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  // ---
  private final Tensor combinedControlPoints;
  private final Tensor controlPoints;
  protected final Tensor controlPointsR;
  protected final boolean closed;
  final Scalar length;
  final int numPoints;
  // for fast lookup
  // using floats
  final float lookupRes = 0.005f;
  final int lookupSkip = 200;
  final float[] posX;
  final float[] posY;

  /** @param combinedControlPoints matrix with dimension n x 3
   * * @param closed */
  public BSplineTrack(Tensor combinedControlPoints, boolean closed) {
    this.combinedControlPoints = combinedControlPoints;
    this.closed = closed;
    numPoints = combinedControlPoints.length();
    List<Integer> from = Arrays.asList(0, 0);
    List<Integer> dims = Arrays.asList(numPoints, 2);
    // TODO ensure control points is matrix MatrixQ and R is VectorQ
    controlPoints = combinedControlPoints.block(from, dims);
    controlPointsR = combinedControlPoints.get(Tensor.ALL, 2);
    int effPoints = numPoints + (closed ? 0 : -1);
    length = RealScalar.of(effPoints);
    // prepare lookup
    posX = new float[(int) (effPoints / lookupRes)];
    posY = new float[(int) (effPoints / lookupRes)];
    for (int i = 0; i < posX.length; ++i) {
      // TODO does getPosition ensure that Control Points are in [m]
      Tensor pos = getPosition(RealScalar.of(i * lookupRes));
      posX[i] = pos.Get(0).number().floatValue();
      posY[i] = pos.Get(1).number().floatValue();
    }
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  public Tensor getControlPoints() {
    return controlPoints.copy();
  }

  public Tensor combinedControlPoints() {
    return combinedControlPoints;
  }

  /** get position at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return position [m] */
  public Tensor getPosition(Scalar pathProgress) {
    Tensor mat = UniformBSpline2.getBasisMatrix(numPoints, Tensors.of(pathProgress), 0, closed);
    return mat.dot(controlPoints).get(0);
  }

  /** get radius at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return radius [m] */
  public Scalar getRadius(Scalar pathProgress) {
    Tensor mat = UniformBSpline2.getBasisMatrix(numPoints, Tensors.of(pathProgress), 0, closed);
    return mat.dot(controlPointsR).Get(0);
  }

  /** get the path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1] */
  public Tensor getDerivation(Scalar pathProgress) {
    Tensor mat = UniformBSpline2.getBasisMatrix(numPoints, Tensors.of(pathProgress), 1, closed);
    return mat.dot(controlPoints).get(0);
  }

  /** get the path direction with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public Tensor getDirection(Scalar pathProgress) {
    return NORMALIZE.apply(getDerivation(pathProgress));
  }

  /** get perpendicular vector to the right of the path
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public Tensor getRightDirection(Scalar pathProgress) {
    Tensor direction = getDerivation(pathProgress);
    return NORMALIZE.apply(Tensors.of(direction.Get(1), direction.Get(0).negate()));
  }

  /** get the 2nd path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1^2] */
  public Tensor get2ndDerivation(Scalar pathProgress) {
    Tensor mat = UniformBSpline2.getBasisMatrix(numPoints, Tensors.of(pathProgress), 2, closed);
    return mat.dot(controlPoints).get(0);
  }

  /** get the curvature
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return curvature unit [1/m] */
  public Scalar getSignedCurvature(Scalar pathProgress) {
    Tensor firstDer = getDerivation(pathProgress);
    Tensor secondDer = get2ndDerivation(pathProgress);
    Scalar under = Power.of(Norm._2.of(firstDer), 3.0);
    return Det2D.of(firstDer, secondDer).divide(under);
  }

  // function local radius is not used/tested and numerically unstable
  // public Scalar getLocalRadius(Scalar pathProgress) {
  // the application of abs() causes a loss of information
  // return getCurvature(pathProgress).abs().reciprocal();
  // }
  Scalar getNearestPathProgress(Tensor position) {
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
      int index = i * lookupSkip;
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
    return RealScalar.of(bestGuess * lookupRes);
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
      int index = i * lookupSkip;
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
    return RealScalar.of(bestGuess * lookupRes);
  }

  float getFastQuadraticDistance(int index, float gPosX, float gPosY) {
    float dx = gPosX - posX[index];
    float dy = gPosY - posY[index];
    // quadratic distances
    return dx * dx + dy * dy;
  }

  private Scalar getDist(Tensor from, Scalar pathProgress) {
    return Norm._2.of(getPosition(pathProgress).subtract(from));
  }

  @Override
  public boolean isInTrack(Tensor position) {
    Scalar prog = getNearestPathProgress(position);
    Scalar dist = getDist(position, prog);
    return Scalars.lessThan(dist, getRadius(prog));
  }

  @Override
  public Tensor getNearestPosition(Tensor position) {
    return getPosition(getNearestPathProgress(position));
  }

  @Override
  public Tensor getMiddleLine(int resolution) {
    Tensor line = Tensors.empty();
    Scalar step = length.divide(RealScalar.of(resolution));
    for (int i = 0; i < resolution; ++i) {
      Scalar prog = RealScalar.of(i).multiply(step);
      line.append(getPosition(prog));
    }
    return line;
  }

  @Override
  public Tensor getLeftLine(int resolution) {
    // this is not accurate for large changes in radius
    Tensor line = Tensors.empty();
    Scalar step = length.divide(RealScalar.of(resolution));
    for (int i = 0; i < resolution; ++i) {
      Scalar prog = RealScalar.of(i).multiply(step);
      Tensor linepos = //
          getPosition(prog) //
              .add(getRightDirection(prog) //
                  .multiply(getRadius(prog)));
      line.append(linepos);
    }
    return line;
  }

  // TODO refactor so that left and right reuse code
  @Override
  public Tensor getRightLine(int resolution) {
    // this is not accurate for large changes in radius
    Tensor line = Tensors.empty();
    Scalar step = length.divide(RealScalar.of(resolution));
    for (int i = 0; i < resolution; ++i) {
      Scalar prog = RealScalar.of(i).multiply(step);
      Tensor linepos = //
          getPosition(prog).//
              subtract(getRightDirection(prog).//
                  multiply(getRadius(prog)));
      line.append(linepos);
    }
    return line;
  }
}
