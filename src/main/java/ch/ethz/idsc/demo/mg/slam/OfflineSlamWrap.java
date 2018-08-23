// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** wrapper to run SLAM algorithm with offline log files */
public class OfflineSlamWrap implements OfflineLogListener {
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final GokartPoseOdometryDemo gokartPoseOdometry = GokartPoseOdometryDemo.create();
  private final SlamTrigger slamTrigger;

  public OfflineSlamWrap(SlamConfig slamConfig) {
    slamTrigger = new SlamTrigger(slamConfig, gokartLidarPose, davisDvsDatagramDecoder, gokartPoseOdometry);
    davisDvsDatagramDecoder.addDvsListener(slamTrigger);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR))
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    if (channel.equals("davis240c.overview.dvs"))
      davisDvsDatagramDecoder.decode(byteBuffer);
    if (channel.equals(RimoLcmServer.CHANNEL_GET))
      gokartPoseOdometry.getEvent(new RimoGetEvent(byteBuffer));
  }
}
