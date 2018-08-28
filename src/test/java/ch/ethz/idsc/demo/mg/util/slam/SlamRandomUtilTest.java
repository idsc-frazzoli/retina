// code by jph
package ch.ethz.idsc.demo.mg.util.slam;

import junit.framework.TestCase;

public class SlamRandomUtilTest extends TestCase {
  public void testSimple() {
    double tg = SlamRandomUtil.getTruncatedGaussian(10, 2, 9.9, 10.1);
    assertTrue(9.9 <= tg);
    assertTrue(tg <= 10.1);
  }
}
