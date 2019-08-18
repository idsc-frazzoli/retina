// code by jph
package ch.ethz.idsc.gokart.offline.map;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.map.OccupancyConfig;
import ch.ethz.idsc.gokart.core.map.OccupancyMappingCore;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class OccupancyMappingOffline implements OfflineLogListener {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  public final Timing timing = Timing.stopped();
  public final Scalar maxTime = Quantity.of(0, SI.SECOND);
  private final OccupancyMappingCore occupancyMappingCore = new OccupancyMappingCore(OccupancyConfig.GLOBAL);

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      occupancyMappingCore.getEvent(gokartPoseEvent);
    } else //
    if (channel.equals(CHANNEL_LIDAR)) {
      timing.start();
      occupancyMappingCore.velodyneDecoder.lasers(byteBuffer);
      timing.stop();
    }
  }

  /** @param radius 0 means no erosion
   * @return */
  public BufferedImageRegion erodedMap(int radius) {
    return occupancyMappingCore.erodedMap(radius);
  }
}
