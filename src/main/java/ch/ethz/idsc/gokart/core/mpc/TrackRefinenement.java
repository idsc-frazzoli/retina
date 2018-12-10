package ch.ethz.idsc.gokart.core.mpc;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Abs;

public class TrackRefinenement {
  public abstract class TrackConstraint{
    Tensor controlPointsX = null;
    Tensor controlPointsY = null;
    Tensor radiusControlPoints = null;
    public abstract void compute(Tensor controlpointsX, Tensor controlpointsY, Tensor radiusControlPoints);
    public Tensor getControlPointsX() {
      return controlPointsX;
    }
    public Tensor getControlPointsY() {
      return controlPointsY;
    }
    public Tensor getRadiusControlPoints() {
      return radiusControlPoints;
    }
  }
  
  public class TrackSplitConstraint extends TrackConstraint{
    final BSplineTrack track;
    Scalar trackProg = null;
    Tensor trackPos = null;
    Tensor trackDirection = null;
    public TrackSplitConstraint(BSplineTrack track) {
      this.track = track;
    }
    @Override
    public void compute(Tensor controlpointsX, Tensor controlpointsY, Tensor radiusControlPoints) {
      Tensor first = Tensors.of(controlpointsX.Get(0),controlpointsX.Get(0));
      Tensor second = Tensors.of(controlpointsX.Get(1),controlpointsX.Get(1));
      Tensor startPos = Mean.of(Tensors.of(first,second));
      if(trackProg == null || trackPos == null || trackDirection == null) {
        trackProg =  track.getFastNearestPathProgress(startPos);
        trackPos = track.getPosition(trackProg);
        trackDirection = track.getDirection(trackProg);
      }
      
      Tensor realVector = second.subtract(first);
      Scalar projection = (Scalar) Min.of(realVector.dot(trackDirection), Quantity.of(0, SI.METER)).divide(RealScalar.of(2));
      Tensor correctedFirst = startPos.subtract(trackDirection.multiply(projection));
      Tensor correctedSecond = startPos.add(trackDirection.multiply(projection));
      this.controlPointsX = controlpointsX;
      this.controlPointsY = controlpointsY;
      this.radiusControlPoints = radiusControlPoints;
      controlpointsX.set(correctedFirst.Get(0), 0);
      controlpointsX.set(correctedSecond.Get(0), 1);
      controlpointsY.set(correctedFirst.Get(1), 0);
      controlpointsY.set(correctedSecond.Get(1), 1);
    }
  }
  
  public TrackRefinenement(PlanableOccupancyGrid occupancyGrid) {
    this.occupancyGrid = occupancyGrid;
  }

  private final PlanableOccupancyGrid occupancyGrid;

  public Tensor getRefinedTrack(Tensor controlPoints, Scalar resolution, int iterations, boolean closed, List<TrackConstraint> constraints) {
    return getRefinedTrack(controlPoints.get(0), controlPoints.get(1), controlPoints.get(2), resolution, iterations, closed, constraints);
  }

  public Tensor getRefinedTrack(Tensor controlpointsX, Tensor controlpointsY, Tensor radiusCtrPoints, Scalar resolution, int iterations, boolean closed, List<TrackConstraint> constraints) {
    int m = (int) (controlpointsX.length() * resolution.number().doubleValue());
    int n = controlpointsX.length();
    Tensor queryPositions;
    if (closed)
      queryPositions = Tensors.vector((i) -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
    else
      queryPositions = Tensors.vector((i) -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m);
    Tensor splineMatrix = MPCBSpline.getBasisMatrix(n, queryPositions, 0, closed);
    Tensor splineMatrixTransp = Transpose.of(splineMatrix);
    Tensor splineMatrix1Der = MPCBSpline.getBasisMatrix(n, queryPositions, 1, closed);
    /* for(int it=0;it<iterations;it++) {
     * Tensor positions = MPCBSpline.getPositions(controlpointsX, controlpointsY, queryPositions, closed, splineMatrix);
     * Tensor sideVectors = MPCBSpline.getSidewardsUnitVectors(controlpointsX, controlpointsY, queryPositions, closed, splineMatrix1Der);
     * Tensor sideLimits = Tensors.vector((i)->getSideLimits(positions.get(i), sideVectors.get(i)),positions.length());
     * } */
    for (int i = 0; i < iterations; i++) {
      System.out.println("Iterate!");
      Tensor corr = getCorrectionVectors(controlpointsX, controlpointsY, radiusCtrPoints, queryPositions, splineMatrix, splineMatrix1Der, resolution, closed);
      if (corr == null)
        return null;
      radiusCtrPoints = radiusCtrPoints.add(splineMatrixTransp.dot(corr.get(2)));
      controlpointsX = controlpointsX.add(splineMatrixTransp.dot(corr.get(0)));
      controlpointsY = controlpointsY.add(splineMatrixTransp.dot(corr.get(1)));
      final Tensor fControl = radiusCtrPoints;
      radiusCtrPoints = Tensors.vector((ii) -> fControl.get(ii).add(gdRadiusGrowth), radiusCtrPoints.length());
      controlpointsX = controlpointsX.add(getRegularization(controlpointsX, gdRegularizer, closed));
      controlpointsY = controlpointsY.add(getRegularization(controlpointsY, gdRegularizer, closed));
      radiusCtrPoints = radiusCtrPoints.add(getRegularization(radiusCtrPoints, gdRegularizer, closed));
      if(constraints!=null) {
        for(TrackConstraint constraint: constraints) {
          constraint.compute(controlpointsX, controlpointsY, radiusCtrPoints);
          controlpointsX = constraint.getControlPointsX();
          controlpointsY = constraint.getControlPointsY();
          radiusCtrPoints = constraint.getRadiusControlPoints();
        }
      }
    }
    // MPCBSplineTrack track = new MPCBSplineTrack(controlpointsX, controlpointsY, radiusCtrPoints);
    return Tensors.of(controlpointsX, controlpointsY, radiusCtrPoints);
  }

  // for debugging
  ArrayList<Tensor> freeLines = new ArrayList<>();
  final Scalar gdLimits = RealScalar.of(0.4);
  final Scalar gdRadius = RealScalar.of(0.8);
  final Scalar gdRadiusGrowth = Quantity.of(0.1, SI.METER);
  final Scalar gdRegularizer = RealScalar.of(0.02);
  final Scalar defaultRadius = Quantity.of(1, SI.METER);

  private Tensor getCorrectionVectors(Tensor controlpointsX, Tensor controlpointsY, Tensor radiusControlPoints, Tensor queryPositions, Tensor basisMatrix,
      Tensor basisMatrix1Der, Scalar resolution, boolean closed) {
    Tensor positions = MPCBSplineMap.getPositions(controlpointsX, controlpointsY, queryPositions, closed, basisMatrix);
    Tensor sideVectors = MPCBSplineMap.getSidewardsUnitVectors(controlpointsX, controlpointsY, queryPositions, closed, basisMatrix1Der);
    Tensor radii = basisMatrix.dot(radiusControlPoints);
    Scalar stepsSize = Quantity.of(0.1, SI.METER);
    freeLines = new ArrayList<>();
    Tensor sideLimits = Tensors.vector((i) -> getSideLimits(positions.get(i), sideVectors.get(i), stepsSize, Quantity.of(1, SI.METER)), positions.length());
    boolean hasNoSolution = sideLimits.stream().anyMatch(row -> row.get(0).equals(row.get(1)));
    if (hasNoSolution)
      return null;
    // upwardsforce
    Tensor lowClipping = Tensors.vector((i) -> Max.of(sideLimits.get(i).Get(0).add(radii.Get(i)), Quantity.of(0, SI.METER)), queryPositions.length());
    Tensor highClipping = Tensors.vector((i) -> Max.of(radii.Get(i).subtract(sideLimits.get(i).Get(1)), Quantity.of(0, SI.METER)), queryPositions.length());
    Tensor sideCorr = lowClipping.subtract(highClipping).multiply(gdLimits.divide(resolution));
    Tensor posCorr = Transpose.of(sideCorr.pmul(sideVectors));
    Tensor radiusCorr = highClipping.add(lowClipping).multiply(gdRadius.divide(resolution)).negate();
    // Tensor upwardsforce = Tensors.vector(list)
    return Tensors.of(posCorr.get(0), posCorr.get(1), radiusCorr);
  }

  private Tensor getRegularization(Tensor controlpoints, Scalar reg, boolean closed) {
    Tensor regVec = Tensors.empty();
    if (!closed) {
      // do we have convolution?
      regVec.append(Quantity.of(0, SI.METER));
      for (int i = 1; i < controlpoints.length() - 1; i++) {
        regVec.append(Mean.of(//
            Tensors.of(controlpoints.Get(i - 1), controlpoints.Get(i + 1))));
      }
    } else {
      // do we have convolution?
      regVec.append(Mean.of(//
          Tensors.of(controlpoints.Get(controlpoints.length() - 1), controlpoints.Get(1))));
      for (int i = 1; i < controlpoints.length() - 1; i++) {
        regVec.append(Mean.of(//
            Tensors.of(controlpoints.Get(i - 1), controlpoints.Get(i + 1))));
      }
      regVec.append(Mean.of(//
          Tensors.of(controlpoints.Get(controlpoints.length() - 2), controlpoints.Get(0))));
    }
    return regVec.subtract(controlpoints).multiply(reg);
  }

  public Tensor getSideLimits(Tensor pos, Tensor sidedir, Scalar stepsSize, Scalar maxSearch) {
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
      occupied = occupancyGrid.isMember(testPosition);
      if (Scalars.lessThan(maxSearch, Abs.of(sideStep)))
        return Tensors.of(RealScalar.ZERO, RealScalar.ZERO);
    }
    // search in both directions for occupied cell
    // only for debugging
    Tensor freeline = Tensors.empty();
    // negative direction
    while (!occupied) {
      sideStep = sideStep.subtract(stepsSize);
      testPosition = pos.add(sidedir.multiply(sideStep));
      occupied = occupancyGrid.isMember(testPosition);
    }
    freeline.append(testPosition);
    lowPosition = sideStep;
    // negative direction
    occupied = false;
    while (!occupied) {
      sideStep = sideStep.add(stepsSize);
      testPosition = pos.add(sidedir.multiply(sideStep));
      occupied = occupancyGrid.isMember(testPosition);
    }
    highPosition = sideStep;
    freeline.append(testPosition);
    freeLines.add(freeline);
    return Tensors.of(lowPosition, highPosition);
  }
}
