// code by jph
package ch.ethz.idsc.retina.offline.slam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

class BrakeDistanceAnalysis implements OfflineLogListener {
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final OfflineLocalize offlineLocalize;
  private final TensorBuilder tensorBuilder = new TensorBuilder();
  private RimoGetEvent rge;
  private LinmotGetEvent lge;

  public BrakeDistanceAnalysis(OfflineLocalizeResource olr) {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    offlineLocalize = new SlamLidarRayBlockListener(olr.model());
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
        tensorBuilder.flatten( //
            time.map(Magnitude.SECOND), //
            offlineLocalize.getPositionVector(), //
            lge.getActualPosition().map(Magnitude.METER), //
            lge.getDemandPosition().map(Magnitude.METER), //
            rge.getAngularRate_Y_pair().map(Magnitude.ANGULAR_RATE), //
            ChassisGeometry.GLOBAL.tangentSpeed(rge).map(Magnitude.VELOCITY) //
        );
      }
    } else //
    if (channel.equals("vlp16.center.ray")) { // TODO redundant
      offlineLocalize.setTime(time);
      velodyneDecoder.lasers(byteBuffer);
    }
  }

  public static void main(String[] args) throws IOException {
    OfflineLocalizeResource olr = OfflineLocalizeResources.BRAKE6;
    // ---
    BrakeDistanceAnalysis brakeDistanceAnalysis = new BrakeDistanceAnalysis(olr);
    OfflineLogPlayer.process(olr.file(), brakeDistanceAnalysis);
    Export.of(UserHome.file("brake6.csv"), brakeDistanceAnalysis.tensorBuilder.getTensor().map(CsvFormat.strict()));
  }
}
