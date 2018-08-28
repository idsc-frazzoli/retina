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
  private static final String CHANNEL_DVS = "davis240c.overview.dvs";
  // ---
  private final GokartPoseLcmLidar gokartPoseLcmLidar = new GokartPoseLcmLidar();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final GokartPoseOdometryDemo gokartPoseOdometryDemo = GokartPoseOdometryDemo.create();

  public OfflineSlamWrap(SlamConfig slamConfig) {
    SlamTrigger slamTrigger = new SlamTrigger(slamConfig, gokartPoseLcmLidar, davisDvsDatagramDecoder, gokartPoseOdometryDemo);
    davisDvsDatagramDecoder.addDvsListener(slamTrigger);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR))
      gokartPoseLcmLidar.getEvent(new GokartPoseEvent(byteBuffer));
    else //
    if (channel.equals(CHANNEL_DVS))
      davisDvsDatagramDecoder.decode(byteBuffer);
    else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET))
      gokartPoseOdometryDemo.getEvent(new RimoGetEvent(byteBuffer));
  }
}
