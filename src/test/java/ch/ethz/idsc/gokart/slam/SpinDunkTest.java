// code by jph
package ch.ethz.idsc.gokart.slam;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.slam.OfflineLocalize;
import ch.ethz.idsc.gokart.offline.slam.SpinLidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SpinDunkTest extends TestCase {
  public void testSimple() throws Exception {
    GokartLogInterface olr = new GokartLogAdapter(new File("src/test/resources/localization/vlp16.center.ray_autobox.rimo.get"));
    // ---
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    OfflineLocalize offlineLocalize = new SpinLidarRayBlockListener(olr.model());
    lidarAngularFiringCollector.addListener(offlineLocalize);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        if (channel.equals("vlp16.center.ray")) {
          offlineLocalize.setTime(time);
          velodyneDecoder.lasers(byteBuffer);
        }
      }
    };
    OfflineLogPlayer.process(olr.file(), offlineLogListener);
    // assertEquals(offlineLocalize.skipped.length(), 1);
    Clip clip = Clip.function(0.38, 1);
    // System.out.println(offlineLocalize.getTable().get(Tensor.ALL, 7));
    assertTrue(offlineLocalize.getTable().get(Tensor.ALL, 7).stream().map(Scalar.class::cast).allMatch(clip::isInside));
  }
}
