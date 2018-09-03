// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/** directly sets the pose estimate of the SLAM algorithm. Can be used if an accurate external pose is available, e.g. through lidar */
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
