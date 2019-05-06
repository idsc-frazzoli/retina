// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.pose.PoseInterface;

/** directly sets the pose estimate of the SLAM algorithm. Can be used if an accurate external pose is available, e.g. through lidar */
/* package */ class SlamLocalizationStep extends PeriodicSlamStep {
  private final PoseInterface poseInterface;

  protected SlamLocalizationStep(SlamCoreContainer slamCoreContainer, PoseInterface gokartPoseInterface) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.localizationUpdateRate);
    this.poseInterface = gokartPoseInterface;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    slamCoreContainer.setPose(poseInterface.getPose());
  }
}
