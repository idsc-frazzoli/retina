// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

import junit.framework.TestCase;

public class Hdl32eSpacialProviderTest extends TestCase {
  public void testSimple() {
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.setLimitLo(2.0);
    assertEquals(hdl32eSpacialProvider.getLimitLo(), 2.0);
  }
}
