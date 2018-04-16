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

  public void testTiming() {
    ComputerSensorsModule csm = new ComputerSensorsModule();
    Stopwatch total = Stopwatch.started();
    for (int count = 0; count < 60; ++count) {
      Stopwatch stopwatch = Stopwatch.started();
      csm.sensor();
      long nanos = stopwatch.display_nanoSeconds();
      assertTrue(nanos < 300_000_000);
    }
    long totals = total.display_nanoSeconds();
    // System.out.println(totals);
    assertTrue(totals < 1_000_000_000);
  }

  public void testSensors() {
    ComputerSensorsModule csm = new ComputerSensorsModule();
    byte[] data = csm.sensor();
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    assertEquals(byteBuffer.get(), 0);
    int cpus = byteBuffer.get();
    if (cpus == 0) {
      // travis has 0 cpu's
      System.out.println("no cpu's");
      return;
    }
    assertTrue(0 < cpus);
    byteBuffer.get(); // gpu size
    assertTrue(0 < byteBuffer.get());
    while (0 < byteBuffer.remaining())
      assertTrue(0 < byteBuffer.get());
  }
}
