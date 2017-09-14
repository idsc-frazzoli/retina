// code by jph
package ch.ethz.idsc.retina.util.io;

import java.util.Objects;

import junit.framework.TestCase;

public class DatagramSocketManagerTest extends TestCase {
  public void testSimple() throws Exception {
    DatagramSocketManager udc = DatagramSocketManager.local(new byte[1000], 18769);
    udc.start();
    Thread.sleep(200);
    assertFalse(udc.datagramSocket().isClosed());
    udc.stop();
    assertTrue(Objects.isNull(udc.datagramSocket()));
  }
}
