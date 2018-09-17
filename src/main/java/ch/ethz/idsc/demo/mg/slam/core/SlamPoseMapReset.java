// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** resets the estimated pose if we are too close to the map boundary. Useful for reactive map modes
 * when we are not interested in absolute pose. occurrence map is also updated accordingly */
// TODO find a way to trigger module based on vehicle pose
/* package */ class SlamPoseMapReset extends PeriodicSlamStep {
  private final Tensor corner;
  private final Tensor cornerHigh;
  private final Scalar resetPoseX;
  private final Scalar resetPoseY;
  private final double padding;

  protected SlamPoseMapReset(SlamCoreContainer slamCoreContainer, SlamCoreConfig slamConfig) {
    super(slamCoreContainer, slamConfig.poseMapUpdateRate);
    corner = slamConfig.corner;
    cornerHigh = slamConfig.cornerHigh();
    resetPoseX = slamConfig.resetPoseX;
    resetPoseY = slamConfig.resetPoseY;
    padding = slamConfig.padding.number().doubleValue();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    if (SlamPoseMapResetUtil.checkBoarders(GokartPoseHelper.attachUnits(slamCoreContainer.getPoseUnitless()), corner, cornerHigh, padding)) {
      Tensor resetPose = Tensors.of(resetPoseX, resetPoseY, slamCoreContainer.getPoseUnitless().Get(2));
      Tensor poseDifference = slamCoreContainer.getPoseUnitless().subtract(resetPose);
      slamCoreContainer.setPoseUnitless(resetPose);
      SlamPoseMapResetUtil.resetPose(slamCoreContainer.getSlamParticles(), poseDifference);
      SlamPoseMapResetUtil.resetMap(slamCoreContainer.getOccurrenceMap(), poseDifference);
    }
  }
}
