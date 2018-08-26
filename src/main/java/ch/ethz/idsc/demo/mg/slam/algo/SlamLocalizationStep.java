// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/** executes the localization step of the SLAM algorithm for the case that the pose is provided from another module,
 * e.g. lidar or odometry */
// TODO maybe not required to set pose for each event since pose update rate depends on input module
/* package */ class SlamLocalizationStep extends EventActionSlamStep {
  private final GokartPoseInterface gokartPoseInterface;

  protected SlamLocalizationStep(SlamContainer slamContainer, GokartPoseInterface gokartPoseInterface) {
    super(slamContainer);
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override
  void davisDvsAction() {
    slamContainer.getSlamEstimatedPose().setPose(gokartPoseInterface.getPose()); // set pose
  }
}
