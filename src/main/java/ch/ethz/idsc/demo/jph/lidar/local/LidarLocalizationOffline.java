// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationCore;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** emulation of the lidar/imu-based localization method on log files */
/* package */ class LidarLocalizationOffline implements OfflineLogListener, LidarRayBlockListener {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore();
  final List<GokartPoseListener> gokartPoseListeners = new LinkedList<>();

  /** @param pose {x[m], y[m], heading} at start of log */
  public LidarLocalizationOffline(Tensor pose) {
    lidarLocalizationCore.lidarAngularFiringCollector.addListener(this);
    lidarLocalizationCore.setTracking(true);
    lidarLocalizationCore.resetPose(pose);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel()))
      lidarLocalizationCore.vmu931ImuFrame(new Vmu931ImuFrame(byteBuffer));
    else //
    if (channel.equals(CHANNEL_LIDAR))
      lidarLocalizationCore.velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = lidarLocalizationCore.createPoseEvent();
      gokartPoseListeners.forEach(listener -> listener.getEvent(gokartPoseEvent));
    }
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    lidarLocalizationCore.run();
  }

  public LidarLocalizationCore lidarLocalizationCore() {
    return lidarLocalizationCore;
  }
}
