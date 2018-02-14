// code by jph
package ch.ethz.idsc.retina.offline.slam;

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
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum OfflineLocalizeDemo {
  ;
  public static void main(String[] args) throws IOException {
    OfflineLocalizeResource olr = OfflineLocalizeResources.OVAL;
    // ---
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    OfflineLocalize offlineLocalize = new SlamLidarRayBlockListener(olr.model());
    lidarAngularFiringCollector.addListener(offlineLocalize);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        if (Scalars.lessThan(Magnitude.SECOND.apply(time), RealScalar.of(20))) {
          if (channel.equals("vlp16.center.ray")) {
            offlineLocalize.setTime(time);
            velodyneDecoder.lasers(byteBuffer);
          }
        }
      }
    };
    OfflineLogPlayer.process(olr.file(), offlineLogListener);
    Export.of(UserHome.file("oval.csv"), offlineLocalize.getTable().map(CsvFormat.strict()));
  }
}
