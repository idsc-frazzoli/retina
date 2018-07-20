// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** A SLAM algorithm "wrapper" to run the algorithm offline. DVS Events, wheel odometry
 * and lidar pose are provided to the SLAM algorithm */
class OfflineSlamWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder;
  private final GokartPoseOdometryDemo gokartOdometryPose;
  private final GokartPoseLcmLidar gokartLidarPose;
  private final SlamProvider slamProvider;
  private final SlamViewer slamViewer;
  private final String imagePrefix;
  private boolean isInitialized;

  public OfflineSlamWrap(SlamConfig slamConfig) {
    slamViewer = new SlamViewer(slamConfig);
    davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
    gokartOdometryPose = GokartPoseOdometryDemo.create();
    gokartLidarPose = new GokartPoseLcmLidar();
    slamProvider = new SlamProvider(slamConfig, gokartOdometryPose, gokartLidarPose);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
    imagePrefix = slamConfig.davisConfig.logFileName;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    double timeInst = time.number().doubleValue();
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
      if (!isInitialized) {
        slamViewer.initialize(timeInst);
        gokartOdometryPose.initializePose(gokartLidarPose.getPose());
        isInitialized = true;
      }
    }
    if (channel.equals("davis240c.overview.dvs")) {
      if (isInitialized)
        davisDvsDatagramDecoder.decode(byteBuffer);
    }
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
    }
    slamViewer.visualize(slamProvider, gokartLidarPose, timeInst);
  }

  public SlamProvider getSlamProvider() {
    return slamProvider;
  }
}
