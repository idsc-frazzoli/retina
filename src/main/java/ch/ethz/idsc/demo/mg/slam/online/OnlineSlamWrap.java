// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** initializes SlamProvider with live listeners */
class OnlineSlamWrap implements StartAndStoppable {
  private final GokartPoseOdometryDemo gokartOdometryPose = GokartPoseOdometryDemo.create();
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final SlamConfig slamConfig = new SlamConfig();
  private final SlamProvider slamProvider = new SlamProvider(slamConfig, gokartOdometryPose, gokartLidarPose);
  private final SlamViewer slamViewer = new SlamViewer(slamConfig, slamProvider, gokartLidarPose);

  OnlineSlamWrap() {
    rimoGetLcmClient.addListener(gokartOdometryPose);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamProvider);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamViewer);
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
