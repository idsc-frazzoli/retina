// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.PoseScatterImage;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.SlamOfflineLocalize;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class BrakeDistanceTable implements OfflineTableSupplier {
  private static final String LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final OfflineLocalize offlineLocalize;
  // ---
  private RimoGetEvent rge;
  private LinmotGetEvent lge;

  public BrakeDistanceTable(GokartLogInterface gokartLogInterface) {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    // LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.planarEmulatorVlp16_p01deg();
    LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    ScatterImage scatterImage = new PoseScatterImage(predefinedMap);
    offlineLocalize = new SlamOfflineLocalize(predefinedMap.getImageExtruded(), gokartLogInterface.pose(), scatterImage);
    lidarAngularFiringCollector.addListener(offlineLocalize);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      rge = new RimoGetEvent(byteBuffer);
    } else //
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      lge = new LinmotGetEvent(byteBuffer);
      // System.out.println(offlineLocalize.getPositionVector());
      if (Objects.nonNull(rge)) {
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), //
            offlineLocalize.getPositionVector(), //
            lge.getActualPosition().map(Magnitude.METER), //
            lge.getDemandPosition().map(Magnitude.METER), //
            rge.getAngularRate_Y_pair().map(Magnitude.PER_SECOND), //
            ChassisGeometry.GLOBAL.odometryTangentSpeed(rge).map(Magnitude.VELOCITY) //
        );
      }
    } else //
    if (channel.equals(LIDAR)) {
      offlineLocalize.setTime(time);
      velodyneDecoder.lasers(byteBuffer);
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
