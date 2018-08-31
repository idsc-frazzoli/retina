// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.AbstractSlamWrap;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** wrapper to run SLAM algorithm with live event stream */
/* package */ class OnlineSlamWrap extends AbstractSlamWrap implements StartAndStoppable {
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();

  OnlineSlamWrap(SlamConfig slamConfig) {
    super(slamConfig);
    rimoGetLcmClient.addListener(gokartOdometryPose);
  }

  @Override // from StartAndStoppable
  public void start() {
    rimoGetLcmClient.startSubscriptions();
    gokartLidarPose.gokartPoseLcmClient.startSubscriptions();
    davisLcmClient.startSubscriptions();
  }

  @Override // from StartAndStoppable
  public void stop() {
    rimoGetLcmClient.stopSubscriptions();
    gokartLidarPose.gokartPoseLcmClient.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
    slamViewer.stop();
  }
}
