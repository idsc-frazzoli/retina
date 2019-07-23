// code by mh
package ch.ethz.idsc.gokart.core.track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.spline.BSpline2Vector;
import ch.ethz.idsc.sophus.flt.ga.Regularization2Step;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Abs;

public class TrackRefinement {
  private final Region<Tensor> region;

  public TrackRefinement(Region<Tensor> region) {
    this.region = region;
  }

  private static final Scalar gdRadiusGrowth = Quantity.of(0.07, SI.METER);
  private static final Scalar gdRegularizer = RealScalar.of(0.007);
  private static final Scalar gdLimits = RealScalar.of(1);
  private static final Scalar gdRadius = RealScalar.of(1);
  private static final TensorUnaryOperator REGULARIZATION_CYCLIC = Regularization2Step.cyclic(RnGeodesic.INSTANCE, gdRegularizer);
  private static final TensorUnaryOperator REGULARIZATION_STRING = Regularization2Step.string(RnGeodesic.INSTANCE, gdRegularizer);

  Tensor getRefinedTrack(Tensor points_xyr, Scalar resolution, int iterations, boolean closed) {
    int m = (int) (points_xyr.length() * resolution.number().doubleValue());
    int n = points_xyr.length();
    Tensor queryPositions;
    if (closed)
      queryPositions = Tensors.vector(i -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
    else
      // TODO MH try Subdivide.of(0, n-2, m-1) for the below
      // FIXME m-1 vs m
      queryPositions = Tensors.vector(i -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m - 1);
    Tensor splineMatrix = queryPositions.map(BSpline2Vector.of(n, 0, closed));
    Tensor splineMatrixTransp = Transpose.of(splineMatrix);
    Tensor splineMatrix1Der = queryPositions.map(BSpline2Vector.of(n, 1, closed));
    /* for(int it=0;it<iterations;it++) {
     * Tensor positions = MPCBSpline.getPositions(controlpointsX, controlpointsY, queryPositions, closed, splineMatrix);
     * Tensor sideVectors = MPCBSpline.getSidewardsUnitVectors(controlpointsX, controlpointsY, queryPositions, closed, splineMatrix1Der);
     * Tensor sideLimits = Tensors.vector((i)->getSideLimits(positions.get(i), sideVectors.get(i)), positions.length());
     * } */
    System.out.println("Iterate " + iterations + " times!");
    for (int i = 0; i < iterations; ++i) {
      Optional<Tensor> optional = getCorrectionVectors(points_xyr, //
          queryPositions, splineMatrix, splineMatrix1Der, resolution, closed);
      if (!optional.isPresent())
        return null;
      Tensor correct = optional.get();
      points_xyr = points_xyr.add(splineMatrixTransp.dot(correct));
      points_xyr.set(gdRadiusGrowth::add, Tensor.ALL, 2);
      points_xyr = closed //
          ? REGULARIZATION_CYCLIC.apply(points_xyr)
          : REGULARIZATION_STRING.apply(points_xyr);
      // ---
      // constraints are not used at the moment
      // {
      // Tensor controlpointsX = points_xyr.get(Tensor.ALL, 0);
      // Tensor controlpointsY = points_xyr.get(Tensor.ALL, 1);
      // Tensor radiusCtrPoints = points_xyr.get(Tensor.ALL, 2);
      // for (TrackConstraint constraint : constraints) {
      // constraint.compute(controlpointsX, controlpointsY, radiusCtrPoints);
      // controlpointsX = constraint.getControlPointsX();
      // controlpointsY = constraint.getControlPointsY();
      // radiusCtrPoints = constraint.getRadiusControlPoints();
      // }
      // points_xyr = Transpose.of(Tensors.of(controlpointsX, controlpointsY, radiusCtrPoints));
      // }
    }
    // MPCBSplineTrack track = new MPCBSplineTrack(controlpointsX, controlpointsY, radiusCtrPoints);
    return points_xyr;
  }

  // for debugging
  // TODO JPH/MH design is bad
  private List<Tensor> freeLines = new ArrayList<>();

  /** .
   * @param points_xyr
   * @param queryPositions
   * @param basisMatrix
   * @param basisMatrix1Der
   * @param resolution
   * @param closed
   * @return */
  private Optional<Tensor> getCorrectionVectors( //
      Tensor points_xyr, Tensor queryPositions, Tensor basisMatrix, //
      Tensor basisMatrix1Der, Scalar resolution, boolean closed) {
    // ---
    Tensor positionsXYR = basisMatrix.dot(points_xyr);
    Tensor sideVectors = BSplineUtil.getSidewardsUnitVectors( //
        Tensor.of(points_xyr.stream().map(Extract2D.FUNCTION)), //
        basisMatrix1Der);
    Scalar stepsSize = Quantity.of(0.1, SI.METER);
    freeLines = new ArrayList<>();
    Tensor sideLimits = Tensors.vector(
        i -> getSideLimits(Extract2D.FUNCTION.apply(positionsXYR.get(i)), sideVectors.get(i), stepsSize, Quantity.of(1, SI.METER)), positionsXYR.length());
    boolean hasNoSolution = sideLimits.stream().anyMatch(row -> row.get(0).equals(row.get(1)));
    if (hasNoSolution)
      return Optional.empty();
    // upwardsforce
    Tensor lowClipping = Tensors.vector(i -> Max.of(sideLimits.Get(i, 0).add(positionsXYR.Get(i, 2)), Quantity.of(0, SI.METER)), queryPositions.length());
    Tensor highClipping = Tensors.vector(i -> Max.of(positionsXYR.Get(i, 2).subtract(sideLimits.Get(i, 1)), Quantity.of(0, SI.METER)), queryPositions.length());
    Tensor sideCorr = lowClipping.subtract(highClipping).multiply(gdLimits.divide(resolution));
    // Tensor posCorr = Transpose.of(sideCorr.pmul(sideVectors));
    // System.out.println("posCorr=" + Dimensions.of(posCorr));
    // Tensor radiusCorr = highClipping.add(lowClipping).multiply(gdRadius.divide(resolution)).negate();
    // // Tensor upwardsforce = Tensors.vector(list)
    // return Optional.of(Transpose.of(Tensors.of(posCorr.get(0), posCorr.get(1), radiusCorr)));
    Tensor posCorr = sideCorr.pmul(sideVectors);
    Tensor radiusCorr = highClipping.add(lowClipping).multiply(gdRadius.divide(resolution).negate());
    return Optional.of(Tensor.of(IntStream.range(0, posCorr.length()) //
        .mapToObj(i -> posCorr.get(i).append(radiusCorr.get(i)))));
  }

  private Tensor getSideLimits(Tensor pos, Tensor sidedir, Scalar stepsSize, Scalar maxSearch) {
    // find free space
    Scalar sideStep = Quantity.of(-0.001, SI.METER);
    Tensor testPosition = null;
    Tensor lowPosition;
    Tensor highPosition;
    boolean occupied = true;
    while (occupied) {
      if (Scalars.lessThan(sideStep, Quantity.of(0, SI.METER)))
        sideStep = sideStep.negate();
      else
        sideStep = sideStep.add(stepsSize).negate();
      testPosition = pos.add(sidedir.multiply(sideStep));
      occupied = region.isMember(testPosition);
      if (Scalars.lessThan(maxSearch, sideStep.abs()))
        return Tensors.of(RealScalar.ZERO, RealScalar.ZERO);
    }
    // TODO JPH search in both directions for occupied cell
    // only for debugging
    Tensor freeline = Tensors.empty();
    // negative direction
    while (!occupied && Scalars.lessThan(Abs.of(sideStep), Quantity.of(10, SI.METER))) {
      sideStep = sideStep.subtract(stepsSize);
      testPosition = pos.add(sidedir.multiply(sideStep));
      occupied = region.isMember(testPosition);
    }
    freeline.append(testPosition);
    lowPosition = sideStep;
    // negative direction
    occupied = false;
    while (!occupied && Scalars.lessThan(Abs.of(sideStep), Quantity.of(10, SI.METER))) {
      sideStep = sideStep.add(stepsSize);
      testPosition = pos.add(sidedir.multiply(sideStep));
      occupied = region.isMember(testPosition);
    }
    highPosition = sideStep;
    freeline.append(testPosition);
    freeLines.add(freeline);
    return Tensors.of(lowPosition, highPosition);
  }
}
