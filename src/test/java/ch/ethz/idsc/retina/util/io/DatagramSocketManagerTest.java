// code by jph
package ch.ethz.idsc.retina.util.io;

import junit.framework.TestCase;

public class DatagramSocketManagerTest extends TestCase {
  public void testSimple() throws Exception {
    DatagramSocketManager udc = DatagramSocketManager.local(new byte[1000], 18769);
    udc.start();
    Thread.sleep(200);
    if (udc.datagramSocket().isClosed())
      System.out.println("TRAVIS CI: SOCKET IS CLOSED");
    // TODO test if travis can handle then test
    udc.stop();
    assertTrue(udc.datagramSocket().isClosed());
  }
}
