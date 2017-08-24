package ch.ethz.idsc.retina.dev.hdl32e.data;

import junit.framework.TestCase;

public class Hdl32eSpacialProviderTest extends TestCase {
  public void testSimple() {
    Hdl32eSpacialProvider p = new Hdl32eSpacialProvider();
    p.setLimitLo(1.0);
    assertEquals(p.limit_lo, 500);
  }
}
