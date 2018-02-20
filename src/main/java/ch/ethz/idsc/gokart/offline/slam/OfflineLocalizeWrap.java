// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class OfflineLocalizeWrap implements OfflineTableSupplier {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final String CHANNEL_IMU = DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final OfflineLocalize offlineLocalize;

  public OfflineLocalizeWrap(OfflineLocalize offlineLocalize) {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    this.offlineLocalize = offlineLocalize;
    lidarAngularFiringCollector.addListener(offlineLocalize);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR)) {
      offlineLocalize.setTime(time);
      velodyneDecoder.lasers(byteBuffer);
    } else //
    if (channel.equals(CHANNEL_IMU))
      offlineLocalize.imuFrame(new DavisImuFrame(byteBuffer));
  }

  @Override
  public Tensor getTable() {
    return offlineLocalize.getTable();
  }
}
