// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;

import ch.ethz.idsc.owl.data.Stopwatch;
import junit.framework.TestCase;

public class ComputerSensorsModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ComputerSensorsModule csm = new ComputerSensorsModule();
    csm.first();
    csm.runAlgo();
    csm.last();
  }

  public void testSensors() {
    ComputerSensorsModule csm = new ComputerSensorsModule();
    Stopwatch stopwatch = Stopwatch.started();
    byte[] data = csm.sensor();
    long nanos = stopwatch.display_nanoSeconds();
    assertTrue(nanos < 300_000_000);
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    assertEquals(byteBuffer.get(), 0);
    assertTrue(0 < byteBuffer.get());
    byteBuffer.get(); // gpu size
    assertTrue(0 < byteBuffer.get());
    while (0 < byteBuffer.remaining())
      assertTrue(0 < byteBuffer.get());
  }
}
