// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

import junit.framework.TestCase;

public class Urg04lxSpacialProviderTest extends TestCase {
  public void testSimple() {
    Urg04lxSpacialProvider usp = new Urg04lxSpacialProvider(2);
    assertTrue(usp.limit_lo != 0);
    int buf = usp.limit_lo;
    usp.setLimitLo(Urg04lxSpacialProvider.THRESHOLD);
    assertEquals(buf, usp.limit_lo);
  }
}
