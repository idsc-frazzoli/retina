// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.nio.ByteBuffer;
import java.util.Timer;

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

/** wrapper to run the event-based SLAM algorithm offline */
/* package */ class OfflineSlamWrap implements OfflineLogListener {
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final GokartPoseOdometryDemo gokartOdometryPose = GokartPoseOdometryDemo.create();
  // specific to slam
  private final Timer timer;
  private final SlamProvider slamProvider;
  private final SlamViewer slamViewer;

  public OfflineSlamWrap(SlamConfig slamConfig) {
    timer = new Timer();
    slamProvider = new SlamProvider(slamConfig, gokartOdometryPose, gokartLidarPose, timer);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
    slamViewer = new SlamViewer(slamConfig, slamProvider, gokartLidarPose, timer);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    }
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
    }
  }

  public SlamProvider getSlamProvider() {
    return slamProvider;
  }

  public void terminateTimer() {
    timer.cancel();
  }
}
