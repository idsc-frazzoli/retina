// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;

// initializes SlamProvider to work with live data
public class OnlineSlamWrap implements StartAndStoppable {
  private final DavisLcmClient davisLcmClient;
  private final RimoGetLcmClient rimoGetLcmClient;
  private final GokartPoseOdometryDemo gokartOdometryPose;
  private final GokartPoseLcmLidar gokartLidarPose;
  private final SlamProvider slamProvider;
  private final SlamConfig slamConfig;

  OnlineSlamWrap() {
    davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
    rimoGetLcmClient = new RimoGetLcmClient();
    slamConfig = new SlamConfig();
    gokartOdometryPose = GokartPoseOdometryDemo.create();
    gokartLidarPose = new GokartPoseLcmLidar();
    slamProvider = new SlamProvider(slamConfig, gokartOdometryPose, gokartLidarPose);
    // add listeners
    rimoGetLcmClient.addListener(gokartOdometryPose);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamProvider);
  }

  @Override
  public void start() {
    // start GokartOdometryPose listener
    rimoGetLcmClient.startSubscriptions();
    // start GokartPoseLcmLidar listener --> is already set up at object creation?
    // once we receive poseEvents, start DavisDvsEvent listener
    if (gokartLidarPose.getPose() != GokartPoseLocal.INSTANCE.getPose()) {
      davisLcmClient.startSubscriptions();
    }
  }

  @Override
  public void stop() {
    rimoGetLcmClient.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
  }
}
