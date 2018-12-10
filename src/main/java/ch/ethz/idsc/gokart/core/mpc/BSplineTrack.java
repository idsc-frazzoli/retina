// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Power;

public class BSplineTrack implements TrackInterface {
  private static final int SPLINE_ORDER_TRACK = 2;
  private static final int SPLINE_ORDER_RADIUS = 2;
  // ---
  private static final Scalar TOL_B = RealScalar.of(0.1);
  // ---
  protected final Tensor controlPoints;
  protected final Tensor controlPointsR;
  protected final Boolean closed = true;
  final Scalar length;
  final int numPoints;
  final BSplineFunction trackSpline;
  final BSplineFunction trackSplineDerivation;
  final BSplineFunction trackSpline2ndDerivation;
  final BSplineFunction radiusTrackSpline;
  // for fast lookup
  // using floats
  final float lookupRes = 0.005f;
  final int lookupSkip = 200;
  final float[] posX;
  final float[] posY;

  public BSplineTrack(Tensor controlPointsX, Tensor controlPointsY, Tensor radiusControlPoints) {
    // TODO: ensure control points are of same size and [m]
    int toAdd = Max.of(SPLINE_ORDER_TRACK, SPLINE_ORDER_RADIUS) + 2;
    numPoints = controlPointsX.length();
    this.controlPoints = Transpose.of(Tensors.of(controlPointsX, controlPointsY));
    this.controlPointsR = radiusControlPoints.copy();
    final int pathLength = controlPointsX.length();
    length = RealScalar.of(pathLength);
    // add points at the end in order to close the loop
    int next = 0;
    while (toAdd > 0) {
      if (next >= pathLength)
        next = 0;
      this.controlPoints.append(Tensors.of(controlPointsX.get(next), controlPointsY.get(next)));
      // this.controlPointsY.append(controlPointsY.get(next));
      this.controlPointsR.append(radiusControlPoints.get(next));
      next++;
      toAdd--;
    }
    trackSpline = BSplineFunction.of(SPLINE_ORDER_TRACK, controlPoints);
    Tensor devControl = Differences.of(controlPoints);
    trackSplineDerivation = BSplineFunction.of(SPLINE_ORDER_TRACK - 1, devControl);
    Tensor devDevControl = Differences.of(devControl);
    trackSpline2ndDerivation = BSplineFunction.of(SPLINE_ORDER_TRACK - 2, devDevControl);
    radiusTrackSpline = BSplineFunction.of(SPLINE_ORDER_RADIUS, this.controlPointsR);
    // prepare lookup
    if (closed) {
      posX = new float[(int) (controlPointsX.length() / lookupRes)];
      posY = new float[(int) (controlPointsY.length() / lookupRes)];
      for (int i = 0; i < controlPointsX.length() / lookupRes; i++) {
        Tensor pos = getPosition(RealScalar.of(i * lookupRes));
        posX[i] = pos.Get(0).number().floatValue();
        posY[i] = pos.Get(1).number().floatValue();
      }
    } else {
      posX = new float[(int) (controlPointsX.length() / lookupRes)];
      posY = new float[(int) (controlPointsY.length() / lookupRes)];
      for (int i = 0; i < controlPointsX.length() / lookupRes; i++) {
        Tensor pos = getPosition(RealScalar.of(i * lookupRes));
        posX[i] = pos.Get(0).number().floatValue();
        posY[i] = pos.Get(1).number().floatValue();
      }
    }
  }

  public Tensor getControlPoints() {
    return controlPoints.copy();
  }

  private Scalar wrap(Scalar pathProgress) {
    // TODO: check if there any specialized functions in the tensor library
    Scalar offset = Quantity.of(Max.of(SPLINE_ORDER_TRACK, SPLINE_ORDER_RADIUS) / 2.0 - 0.5, SI.ONE);
    Scalar startPoint = Floor.of(pathProgress.subtract(offset).divide(length)).multiply(length);
    return pathProgress.subtract(startPoint);
  }

  /** get position at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return position [m] */
  public Tensor getPosition(Scalar pathProgress) {
    return trackSpline.apply(wrap(pathProgress));
  }

  /** get radius at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return radius [m] */
  public Scalar getRadius(Scalar pathProgress) {
    return (Scalar) radiusTrackSpline.apply(wrap(pathProgress));
  }

  // public Scalar getCurvature(Scalar pathPro)
  /** get the path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1] */
  public Tensor getDerivation(Scalar pathProgress) {
    Scalar devPathProgress = pathProgress.add(Quantity.of(-0.5, SI.ONE));
    return trackSplineDerivation.apply(wrap(devPathProgress));
  }

  /** get the path direction with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public Tensor getDirection(Scalar pathProgress) {
    return Normalize.with(Norm._2).apply(getDerivation(pathProgress));
  }

  /** get perpendicular vector to the right of the path
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return direction of the path [1] */
  public Tensor getRightDirection(Scalar pathProgress) {
    Tensor direction = getDerivation(pathProgress);
    return Normalize.with(Norm._2).apply(Tensors.of(direction.Get(1), direction.Get(0).negate()));
  }

  /** get the 2nd path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1^2] */
  public Tensor get2ndDerivation(Scalar pathProgress) {
    Scalar devPathProgress = pathProgress.add(RealScalar.of(-1));
    return trackSpline2ndDerivation.apply(wrap(devPathProgress));
  }

  /** get the curvature
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return curvature unit [1/m] */
  public Scalar getCurvature(Scalar pathProgress) {
    Tensor firstDer = getDerivation(pathProgress);
    Tensor secondDer = get2ndDerivation(pathProgress);
    Scalar under = Power.of(Norm._2.of(firstDer), 3.0);
    Scalar upper = firstDer.Get(0).multiply(secondDer.Get(1))//
        .subtract(firstDer.Get(1).multiply(secondDer.Get(0)));
    return upper.divide(under);
  }

  public Scalar getLocalRadius(Scalar pathProgress) {
    return RealScalar.ONE.divide(getCurvature(pathProgress).abs());
  }

  public Scalar getNearestPathProgress(Tensor position) {
    // initial guesses
    Scalar bestDist = DoubleScalar.POSITIVE_INFINITY.multiply(Quantity.of(1, SI.METER));
    Scalar bestGuess = RealScalar.ZERO;
    for (int i = 0; i < numPoints; i++) {
      Scalar prog = RealScalar.of(i);
      Scalar dist = Norm._2.of(getPosition(prog).subtract(position));
      if (Scalars.lessThan(dist, bestDist)) {
        bestDist = dist;
        bestGuess = prog;
      }
    }
    return getNearestPathProgress(position, bestGuess, RealScalar.of(0.4));
  }

  /** problem: using normal BSpline implementation takes more time than full MPC optimization
   * solution: fast position lookup: from 45000 micro s -> 15 micro s */
  Scalar getFastNearestPathProgress(Tensor position) {
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
        precision = precision / 2;
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

  protected Scalar getNearestPathProgress(Tensor position, Scalar guess, Scalar precision) {
    while (Scalars.lessThan(TOL_B, precision)) {
      Scalar bestDist = getDist(position, guess);
      Scalar lower = guess.subtract(precision);
      Scalar upper = guess.add(precision);
      if (Scalars.lessThan(getDist(position, lower), bestDist))
        guess = lower;
      else //
      if (Scalars.lessThan(getDist(position, upper), bestDist))
        guess = upper;
      else
        precision = precision.divide(RealScalar.of(2));
      // System.out.println(guess);
      // System.out.println(getDist(position, guess));
    }
    return guess;
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
    for (int i = 0; i < resolution; i++) {
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
    for (int i = 0; i < resolution; i++) {
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
    for (int i = 0; i < resolution; i++) {
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
