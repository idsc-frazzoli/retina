// code by jph
package ch.ethz.idsc.retina.util.io;

import java.util.Objects;

import ch.ethz.idsc.owl.data.Stopwatch;
import junit.framework.TestCase;

public class DatagramSocketManagerTest extends TestCase {
  public void testSimple() throws Exception {
    DatagramSocketManager udc = DatagramSocketManager.local(new byte[1000], 18769);
    Stopwatch stopwatch = Stopwatch.started();
    for (int count = 0; count < 3; ++count) {
      udc.start();
      udc.start();
      assertFalse(udc.datagramSocket().isClosed());
      udc.stop();
      udc.stop();
      assertTrue(Objects.isNull(udc.datagramSocket()));
    }
    // Travis had trouble at 0.1
    assertTrue(stopwatch.display_seconds() < 0.5);
  }
}
