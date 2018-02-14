// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.offline.slam.OfflineLocalize;
import ch.ethz.idsc.retina.offline.slam.OfflineLocalizeResource;
import ch.ethz.idsc.retina.offline.slam.OfflineLocalizeResources;
import ch.ethz.idsc.retina.offline.slam.SlamLidarRayBlockListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SlamDunkTest extends TestCase {
  public void testSimple() throws Exception {
    // global pose is approximately {56.137[m], 57.022[m], -1.09428}
    // new state={56.18474711501799[m], 57.042752703987055[m], -1.0956022539443036}
    OfflineLocalizeResource olr = OfflineLocalizeResources.TEST;
    assertTrue(olr.file().isFile());
    // ---
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    OfflineLocalize offlineLocalize = new SlamLidarRayBlockListener(olr.model2pixel());
    lidarAngularFiringCollector.addListener(offlineLocalize);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        offlineLocalize.setTime(time);
        if (channel.equals("vlp16.center.ray"))
          velodyneDecoder.lasers(byteBuffer);
      }
    };
    OfflineLogPlayer.process(olr.file(), offlineLogListener);
    assertEquals(offlineLocalize.skipped.length(), 1);
    Clip clip = Clip.function(0.4, 1);
    assertTrue(offlineLocalize.getTable().get(Tensor.ALL, 7).stream().map(Scalar.class::cast).allMatch(clip::isInside));
  }
}
