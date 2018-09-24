// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.AbstractSlamWrap;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;

/** wrapper to run SLAM algorithm with live event stream */
/* package */ class OnlineSlamWrap extends AbstractSlamWrap {
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();

  OnlineSlamWrap() {
    rimoGetLcmClient.addListener(gokartOdometryPose);
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
