// code by jph
package ch.ethz.idsc.retina.util.io;

import java.util.Objects;

import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class DatagramSocketManagerTest extends TestCase {
  public void testSimple() throws Exception {
    DatagramSocketManager datagramSocketManager = DatagramSocketManager.local(new byte[1000], 18769);
    Timing timing = Timing.started();
    for (int count = 0; count < 3; ++count) {
      datagramSocketManager.start();
      datagramSocketManager.start();
      assertFalse(datagramSocketManager.datagramSocket().isClosed());
      datagramSocketManager.stop();
      datagramSocketManager.stop();
      assertTrue(Objects.isNull(datagramSocketManager.datagramSocket()));
    }
    // Travis had trouble at 0.1
    // Travis openjdk8 cannot always make 0.5, or 0.7
    // Travis openjdk8 longest so far: 9.117577110000001
    double value = timing.seconds();
    boolean status = 10 <= value;
    if (status)
      System.err.println(value);
    assertFalse(status);
  }
}
