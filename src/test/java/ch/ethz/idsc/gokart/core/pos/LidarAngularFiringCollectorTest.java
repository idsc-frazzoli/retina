// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

class CountLidarRayBlockListener implements LidarRayBlockListener {
  int block_count = 0;

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    int limit_f = lidarRayBlockEvent.floatBuffer.limit();
    int limit_b = lidarRayBlockEvent.byteBuffer.limit();
    TestCase.assertEquals(limit_f, limit_b * 2);
    ++block_count;
  }
}

public class LidarAngularFiringCollectorTest extends TestCase {
  public void testSimple() throws Exception {
    File file = new File("src/test/resources/localization/vlp16.center.ray_autobox.rimo.get", "log.lcm");
    assertTrue(file.isFile());
    // ---
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    // LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.planarEmulatorVlp16_p01deg();
    LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    velodyneDecoder.addRayListener(new LidarRayDataListener() {
      int angle = 0;

      @Override
      public void timestamp(int usec, int type) {
        type = angle; // in order to avoid warning
      }

      @Override
      public void scan(int rotational, ByteBuffer byteBuffer) {
        angle = rotational;
      }
    });
    CountLidarRayBlockListener lidarRayBlockListener = new CountLidarRayBlockListener();
    lidarAngularFiringCollector.addListener(lidarRayBlockListener);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        // System.out.println(time + " " + event.channel);
        if (channel.equals("vlp16.center.ray")) {
          velodyneDecoder.lasers(byteBuffer);
        }
      }
    };
    OfflineLogPlayer.process(file, offlineLogListener);
    assertEquals(lidarRayBlockListener.block_count, 4);
  }
}
