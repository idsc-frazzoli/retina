// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
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
  private final AbstractFilterHandler filterHandler;
  private final SlamConfig slamConfig;
  private final SlamProvider slamProvider;
  private final SlamViewer slamViewer;

  OnlineSlamWrap() {
    slamConfig = new SlamConfig();
    filterHandler = new BackgroundActivityFilter(slamConfig.davisConfig);
    slamProvider = new SlamProvider(slamConfig, filterHandler, gokartLidarPose);
    slamViewer = new SlamViewer(slamConfig, slamProvider.getSlamContainer(), gokartLidarPose);
    rimoGetLcmClient.addListener(gokartOdometryPose);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(filterHandler);
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
