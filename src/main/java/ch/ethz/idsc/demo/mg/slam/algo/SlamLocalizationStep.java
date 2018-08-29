// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/** executes the localization step of the SLAM algorithm for the case that the pose is provided from another module,
 * e.g. lidar or odometry */
// TODO instead of periodic calls, incorporate a GokartPoseListener
/* package */ class SlamLocalizationStep extends PeriodicSlamStep {
  private final GokartPoseInterface gokartPoseInterface;

  protected SlamLocalizationStep(SlamContainer slamContainer, SlamConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    super(slamContainer, slamConfig.localizationUpdateRate);
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    slamContainer.setPose(gokartPoseInterface.getPose());
  }
}
