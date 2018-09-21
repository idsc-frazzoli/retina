// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/** resets the estimated pose if we are too close to the map boundary. Useful for reactive map modes
 * when we are not interested in absolute pose. occurrence map is also updated accordingly */
// TODO find a way to trigger module based on vehicle pose
/* package */ class SlamPoseMapReset extends PeriodicSlamStep {
  private final double padding;
  private final Tensor mapMoveVector;
  private final Tensor mapDimensions;
  // ---
  private Tensor corner;
  private Tensor cornerHigh;

  protected SlamPoseMapReset(SlamCoreContainer slamCoreContainer, SlamCoreConfig slamCoreConfig) {
    super(slamCoreContainer, slamCoreConfig.poseMapUpdateRate);
    padding = slamCoreConfig.padding.number().doubleValue();
    mapMoveVector = Tensors.of( //
        Magnitude.METER.apply(slamCoreConfig.dimX.multiply(RealScalar.of(0.5))), //
        Magnitude.METER.apply(slamCoreConfig.dimY.multiply(RealScalar.of(0.5))));
    mapDimensions = Tensors.of(slamCoreConfig.dimX, slamCoreConfig.dimY);
    corner = slamCoreConfig.corner;
    cornerHigh = slamCoreConfig.cornerHigh();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    Tensor pose = GokartPoseHelper.attachUnits(slamCoreContainer.getPoseUnitless());
    getCorners();
    Tensor poseDifference = SlamPoseMapResetUtil.computePoseDifference(pose, corner, cornerHigh, mapMoveVector, padding);
    if (!Scalars.lessEquals(Norm2Squared.ofVector(poseDifference), RealScalar.of(0)))
      resetStuff(poseDifference);
  }

  private void getCorners() {
    Scalar cornerX = Quantity.of(slamCoreContainer.getOccurrenceMap().getCornerX(), SI.METER);
    Scalar cornerY = Quantity.of(slamCoreContainer.getOccurrenceMap().getCornerY(), SI.METER);
    corner = Tensors.of(cornerX, cornerY);
    cornerHigh = corner.add(mapDimensions);
  }

  // poseDifference without units
  private void resetStuff(Tensor poseDifference) {
    SlamPoseMapResetUtil.resetMap(slamCoreContainer.getOccurrenceMap(), poseDifference);
  }
}
