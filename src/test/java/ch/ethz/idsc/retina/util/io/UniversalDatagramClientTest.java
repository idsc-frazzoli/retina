// code by jph
package ch.ethz.idsc.retina.util.io;

import junit.framework.TestCase;

public class UniversalDatagramClientTest extends TestCase {
  public void testSimple() throws Exception {
    UniversalDatagramClient udc = UniversalDatagramClient.create(new byte[1000], 18769);
    udc.start();
    Thread.sleep(200);
    if (udc.datagramSocket().isClosed())
      System.out.println("TRAVIS CI: SOCKET IS CLOSED");
    // TODO test if travis can handle then test
    udc.stop();
    assertTrue(udc.datagramSocket().isClosed());
  }
}
