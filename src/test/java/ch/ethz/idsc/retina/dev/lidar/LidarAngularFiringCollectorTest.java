// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;
import lcm.logging.Log.Event;

class CountLidarRayBlockListener implements LidarRayBlockListener {
  int block_count = 0;

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    // System.out.println("BLOCK");
    ++block_count;
  }
}

public class LidarAngularFiringCollectorTest extends TestCase {
  public void testSimple() throws Exception {
    File file = new File("src/test/resources/localization", "vlp16.center.ray_autobox.rimo.get.lcm");
    assertTrue(file.isFile());
    // ---
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    velodyneDecoder.addRayListener(new LidarRayDataListener() {
      int angle = 0;

      @Override
      public void timestamp(int usec, int type) {
        // System.out.println(" - ");
        type = angle; // in order to prevent error
      }

      @Override
      public void scan(int rotational, ByteBuffer byteBuffer) {
        angle = rotational;
        // System.out.println(" " + angle);
      }
    });
    CountLidarRayBlockListener lidarRayBlockListener = new CountLidarRayBlockListener();
    lidarAngularFiringCollector.addListener(lidarRayBlockListener);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
        // System.out.println(time + " " + event.channel);
        if (event.channel.equals("vlp16.center.ray")) {
          velodyneDecoder.lasers(byteBuffer);
        }
      }
    };
    OfflineLogPlayer.process(file, offlineLogListener);
    assertEquals(lidarRayBlockListener.block_count, 4);
  }
}
