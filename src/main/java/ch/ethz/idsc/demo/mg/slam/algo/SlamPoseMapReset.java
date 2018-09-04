// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
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

  protected SlamPoseMapReset(SlamContainer slamContainer, SlamConfig slamConfig) {
    super(slamContainer, slamConfig.poseMapUpdateRate);
    corner = slamConfig.corner;
    cornerHigh = slamConfig.cornerHigh();
    resetPoseX = slamConfig.resetPoseX;
    resetPoseY = slamConfig.resetPoseY;
    padding = slamConfig.padding.number().doubleValue();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    if (SlamPoseMapResetUtil.checkBoarders(slamContainer.getPose(), corner, cornerHigh, padding)) {
      Tensor resetPose = Tensors.of(resetPoseX, resetPoseY, slamContainer.getPoseUnitless().Get(2));
      Tensor poseDifference = slamContainer.getPoseUnitless().subtract(resetPose);
      slamContainer.setPoseUnitless(resetPose);
      SlamPoseMapResetUtil.resetPose(slamContainer.getSlamParticles(), poseDifference);
      SlamPoseMapResetUtil.resetMap(slamContainer.getOccurrenceMap(), poseDifference);
    }
  }
}
