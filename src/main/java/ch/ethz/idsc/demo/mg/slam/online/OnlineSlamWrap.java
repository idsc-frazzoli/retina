// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamTrigger;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** wrapper to run SLAM algorithm with live event stream */
/* package */ class OnlineSlamWrap implements StartAndStoppable {
  private final GokartPoseOdometryDemo gokartOdometryPose = GokartPoseOdometryDemo.create();
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  // ---
  private final SlamTrigger slamTrigger;

  OnlineSlamWrap() {
    rimoGetLcmClient.addListener(gokartOdometryPose);
    SlamConfig slamConfig = new SlamConfig();
    slamTrigger = new SlamTrigger(slamConfig, gokartLidarPose, davisLcmClient.davisDvsDatagramDecoder, gokartOdometryPose);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamTrigger);
  }

  @Override
  public void start() {
    rimoGetLcmClient.startSubscriptions();
    gokartLidarPose.gokartPoseLcmClient.startSubscriptions();
    davisLcmClient.startSubscriptions();
  }

  @Override
  public void stop() {
    rimoGetLcmClient.stopSubscriptions();
    gokartLidarPose.gokartPoseLcmClient.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
  }
}
