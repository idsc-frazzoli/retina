// code by mg
package ch.ethz.idsc.demo.mg.slam.offline;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.slam.AbstractSlamWrap;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** wrapper to run SLAM algorithm with offline log files */
/* package */ class OfflineSlamWrap extends AbstractSlamWrap implements OfflineLogListener {
  private static final String CHANNEL_DVS = "davis240c.overview.dvs";

  OfflineSlamWrap() {
    start();
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR))
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    else //
    if (channel.equals(CHANNEL_DVS))
      davisLcmClient.davisDvsDatagramDecoder.decode(byteBuffer);
    else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET))
      gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
  }

  @Override // from AbstractSlamWrap
  protected void protected_start() {
    // ---
  }

  @Override // from AbstractSlamWrap
  protected void protected_stop() {
    slamCoreContainer.stop();
  }
}
