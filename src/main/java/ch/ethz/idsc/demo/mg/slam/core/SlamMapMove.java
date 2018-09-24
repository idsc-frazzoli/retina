// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;

/** moves the part of the world frame which is saved in the occurrence map when the estimated
 * pose comes too close to the borders of the current map */
/* package */ class SlamMapMove extends PeriodicSlamStep {
  private final Tensor mapMoveVector = //
      SlamCoreConfig.GLOBAL.mapDimensions.map(Magnitude.METER).multiply(RationalScalar.HALF);
  private final Tensor mapDimensions = SlamCoreConfig.GLOBAL.mapDimensions;
  private final double padding = SlamCoreConfig.GLOBAL.padding.number().doubleValue();
  // ---
  /** current lower left corner of map */
  private Tensor corner;
  /** current upper right corner of map */
  private Tensor cornerHigh;

  protected SlamMapMove(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer, SlamCoreConfig.GLOBAL.poseMapUpdateRate);
    corner = SlamCoreConfig.GLOBAL.corner;
    cornerHigh = SlamCoreConfig.GLOBAL.cornerHigh();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    Tensor vehiclePosition = GokartPoseHelper.attachUnits(slamCoreContainer.getPoseUnitless()).extract(0, 2);
    getCurrentCorners();
    Tensor positionDifference = SlamMapMoveUtil.computePositionDifference(//
        vehiclePosition, corner, cornerHigh, mapMoveVector, padding);
    // when positionDifference is not zero, we move the map accordingly
    if (!Chop.NONE.allZero(positionDifference))
      slamCoreContainer.getOccurrenceMap().moveMap(positionDifference);
  }

  /** updates the corner fields to the current values */
  private void getCurrentCorners() {
    corner = Tensors.of(//
        Quantity.of(slamCoreContainer.getOccurrenceMap().getCornerX(), SI.METER), //
        Quantity.of(slamCoreContainer.getOccurrenceMap().getCornerY(), SI.METER));
    cornerHigh = corner.add(mapDimensions);
  }
}
