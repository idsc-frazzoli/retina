// code by jph
package ch.ethz.idsc.retina.util.io;

import junit.framework.TestCase;

public class UniversalDatagramClientTest extends TestCase {
  public void testSimple() throws Exception {
    // TODO this does not behave as expected
    UniversalDatagramClient udc = new UniversalDatagramClient(18769, new byte[1000]);
    udc.start();
    udc.stop();
  }
}
