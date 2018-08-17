// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import java.util.Timer;

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
/* package */ class OnlineSlamWrap implements StartAndStoppable {
  private final GokartPoseOdometryDemo gokartOdometryPose = GokartPoseOdometryDemo.create();
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final Timer timer = new Timer();
  // ---
  private final SlamConfig slamConfig;
  private final SlamProvider slamProvider;
  private final SlamViewer slamViewer;

  OnlineSlamWrap() {
    slamConfig = new SlamConfig();
    slamConfig.onlineMode = true; // just to make sure
    slamProvider = new SlamProvider(slamConfig, gokartOdometryPose, gokartLidarPose, timer);
    slamViewer = new SlamViewer(slamConfig, slamProvider, gokartLidarPose, timer);
    rimoGetLcmClient.addListener(gokartOdometryPose);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamProvider);
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
    timer.cancel();
  }
}
