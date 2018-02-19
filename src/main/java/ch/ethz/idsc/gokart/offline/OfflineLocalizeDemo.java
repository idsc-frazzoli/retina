// code by jph
package ch.ethz.idsc.gokart.offline;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

class OfflineLocalizeDemo implements OfflineLogListener {
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final OfflineLocalize offlineLocalize;

  public OfflineLocalizeDemo(OfflineLocalizeResource olr) {
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
    if (channel.equals("vlp16.center.ray")) { // TODO redundant
      offlineLocalize.setTime(time);
      velodyneDecoder.lasers(byteBuffer);
    }
  }

  public static void main(String[] args) throws IOException {
    OfflineLocalizeResource olr = OfflineLocalizeResources.BRAKE6;
    // ---
    OfflineLocalizeDemo offlineLogListener = new OfflineLocalizeDemo(olr);
    OfflineLogPlayer.process(olr.file(), offlineLogListener);
    Export.of(UserHome.file("brake6.csv"), offlineLogListener.offlineLocalize.getTable().map(CsvFormat.strict()));
  }
}
