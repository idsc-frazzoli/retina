package ch.ethz.idsc.gokart.core.mpc;

import java.util.LinkedList;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Power;

public class BSplineTrack {
  final Tensor controlPointsX;
  final Tensor controlPointsY;
  final Tensor radiusControlPoints;
  final Scalar length;
  final int numPoints;
  static int trackSplineOrder = 2;
  static int radiusSplineOrder = 1;
  static Scalar dTol = Quantity.of(0.000001, SI.METER);
  static Scalar bTol = Quantity.of(0.01, SI.ONE);
  final BSplineFunction xTrackSpline;
  final BSplineFunction yTrackSpline;
  final BSplineFunction xTrackSplineDerivation;
  final BSplineFunction yTrackSplineDerivation;
  final BSplineFunction xTrackSpline2ndDerivation;
  final BSplineFunction yTrackSpline2ndDerivation;
  final BSplineFunction radiusTrackSpline;

  public BSplineTrack(Tensor controlPointsX, Tensor controlPointsY, Tensor radiusControlPoints) {
    // TODO: ensure control points are of same size and [m]
    int toAdd = Max.of(trackSplineOrder, radiusSplineOrder) + 2;
    numPoints = controlPointsX.length();
    this.controlPointsX = controlPointsX.copy();
    this.controlPointsY = controlPointsY.copy();
    this.radiusControlPoints = radiusControlPoints.copy();
    final int pathLength = controlPointsX.length();
    length = Quantity.of(pathLength, SI.ONE);
    // add points at the end in order to close the loop
    int next = 0;
    while (toAdd > 0) {
      if (next >= pathLength)
        next = 0;
      this.controlPointsX.append(controlPointsX.get(next));
      this.controlPointsY.append(controlPointsY.get(next));
      this.radiusControlPoints.append(radiusControlPoints.get(next));
      next++;
      toAdd--;
    }
    xTrackSpline = BSplineFunction.of(trackSplineOrder, this.controlPointsX);
    yTrackSpline = BSplineFunction.of(trackSplineOrder, this.controlPointsY);
    LinkedList<Integer> dim = new LinkedList<>();
    dim.add(this.controlPointsX.length() - 1);
    // first derivation
    LinkedList<Integer> start0 = new LinkedList<>();
    start0.add(0);
    LinkedList<Integer> start1 = new LinkedList<>();
    start1.add(1);
    Tensor xDevControl = this.controlPointsX.block(start1, dim)//
        .subtract(this.controlPointsX.block(start0, dim));
    xTrackSplineDerivation = BSplineFunction.of(trackSplineOrder - 1, xDevControl);
    Tensor yDevControl = this.controlPointsY.block(start1, dim)//
        .subtract(this.controlPointsY.block(start0, dim));
    yTrackSplineDerivation = BSplineFunction.of(trackSplineOrder - 1, yDevControl);
    // second derivation
    LinkedList<Integer> ddim = new LinkedList<>();
    ddim.add(this.controlPointsX.length() - 2);
    Tensor xDevDevControl = xDevControl.block(start1, ddim)//
        .subtract(xDevControl.block(start0, ddim));
    xTrackSpline2ndDerivation = BSplineFunction.of(trackSplineOrder - 2, xDevDevControl);
    Tensor yDevDevControl = yDevControl.block(start1, ddim)//
        .subtract(yDevControl.block(start0, ddim));
    yTrackSpline2ndDerivation = BSplineFunction.of(trackSplineOrder - 2, yDevDevControl);
    radiusTrackSpline = BSplineFunction.of(radiusSplineOrder, this.radiusControlPoints);
  }

  private Scalar wrap(Scalar pathProgress) {
    // TODO: check if there any specialized functions in the tensor library
    Scalar offset = Quantity.of(Max.of(trackSplineOrder, radiusSplineOrder) / 2.0 - 0.5, SI.ONE);
    Scalar startPoint = Floor.of(pathProgress.subtract(offset).divide(length)).multiply(length);
    return pathProgress.subtract(startPoint);
  }

  /** get position at a certain path value
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return position [m] */
  public Tensor getPosition(Scalar pathProgress) {
    return Tensors.of(//
        xTrackSpline.apply(wrap(pathProgress)), //
        yTrackSpline.apply(wrap(pathProgress)));
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
    return Tensors.of(//
        xTrackSplineDerivation.apply(wrap(devPathProgress)), //
        yTrackSplineDerivation.apply(wrap(devPathProgress)));
  }

  /** get the 2nd path derivative with respect to path progress
   * 
   * @param pathProgress progress along path
   * corresponding to control point indices [1]
   * @return change rate of position unit [m/1^2] */
  public Tensor get2ndDerivation(Scalar pathProgress) {
    Scalar devPathProgress = pathProgress.add(Quantity.of(-1, SI.ONE));
    return Tensors.of(//
        xTrackSpline2ndDerivation.apply(wrap(devPathProgress)), //
        yTrackSpline2ndDerivation.apply(wrap(devPathProgress)));
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
      Scalar prog = Quantity.of(i, SI.ONE);
      Scalar dist = Norm._2.of(getPosition(prog).subtract(position));
      if (Scalars.lessThan(dist, bestDist)) {
        bestDist = dist;
        bestGuess = prog;
      }
    }
    return getNearestPathProgress(position, bestGuess, RealScalar.ONE);
  }

  /* public Scalar getNearestPathProgress(Tensor position, Scalar guess) {
   * // newton derived method
   * // TODO: fancy method for this (I know one)
   * Scalar errProj = RealScalar.ONE.multiply(Quantity.of(1, SI.METER));
   * while (Scalars.lessThan(dTol, errProj.abs())) {
   * Tensor currPos = getPosition(guess);
   * Tensor pathDer = getDerivation(guess);
   * Scalar pathSpd = Norm._2.of(pathDer);
   * Tensor pathDir = pathDer.divide(pathSpd);
   * Tensor error = currPos.subtract(position);
   * Scalar errorNorm = Norm._2.of(error);
   * Tensor errorDir = error.divide(errorNorm);
   * Scalar localLimit = getLocalRadius(guess).multiply(Quantity.of(0.1, SI.ONE));
   * // project error onto path
   * errProj = (Scalar) error.dot(pathDir);
   * Scalar clippedProjError = Clip.function(localLimit.negate(), localLimit).apply(errProj);
   * // System.out.println(errProj);
   * guess = guess.subtract(clippedProjError.divide(pathSpd).multiply(Quantity.of(0.1, SI.ONE)));
   * }
   * return guess;
   * } */
  Scalar getDist(Tensor from, Scalar pathProgress) {
    return Norm._2.of(getPosition(pathProgress).subtract(from));
  }

  private Scalar getNearestPathProgress(Tensor position, Scalar guess, Scalar precision) {
    while (Scalars.lessThan(bTol, precision)) {
      Scalar bestDist = getDist(position, guess);
      Scalar lower = guess.subtract(precision);
      Scalar upper = guess.add(precision);
      if (Scalars.lessThan(getDist(position, lower), bestDist))
        guess = lower;
      else if (Scalars.lessThan(getDist(position, upper), bestDist))
        guess = upper;
      else
        precision = precision.divide(Quantity.of(2, SI.ONE));
      // System.out.println(getDist(position, guess));
    }
    return guess;
  }

  /** test if the position is inside the track limits
   * 
   * @param position in [m]
   * @return true if within track limits */
  public boolean isInTrack(Tensor position) {
    Scalar prog = getNearestPathProgress(position);
    Scalar dist = getDist(position, prog);
    return Scalars.lessThan(dist, getRadius(prog));
  }

  public Tensor getNearestPosition(Tensor position) {
    return getPosition(getNearestPathProgress(position));
  }
}
