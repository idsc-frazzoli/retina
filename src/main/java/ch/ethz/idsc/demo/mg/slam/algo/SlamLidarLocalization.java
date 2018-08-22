// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** updates the pose estimate using the provided lidar pose estimate. Useful for lidarMappingMode */
/* package */ class SlamLidarLocalization extends AbstractSlamStep {
  private final GokartPoseInterface gokartLidarPose;

  protected SlamLidarLocalization(SlamContainer slamContainer, GokartPoseInterface gokartLidarPose) {
    super(slamContainer);
    this.gokartLidarPose = gokartLidarPose;
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    setLidarPose();
  }

  private void setLidarPose() {
    slamContainer.getSlamEstimatedPose().setPose(gokartLidarPose.getPose());
  }
}
