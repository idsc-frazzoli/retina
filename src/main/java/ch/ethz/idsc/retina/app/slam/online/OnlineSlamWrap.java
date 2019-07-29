// code by mg
package ch.ethz.idsc.retina.app.slam.online;

import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.app.slam.AbstractSlamWrap;

/** wrapper to run SLAM algorithm with live event stream */
/* package */ class OnlineSlamWrap extends AbstractSlamWrap {
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();

  OnlineSlamWrap() {
    rimoGetLcmClient.addListener(gokartPoseOdometryDemo);
  }

  @Override // from AbstractSlamWrap
  protected void protected_start() {
    rimoGetLcmClient.startSubscriptions();
  }

  @Override // from AbstractSlamWrap
  protected void protected_stop() {
    rimoGetLcmClient.stopSubscriptions();
  }
}
