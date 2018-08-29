// code by jph
package ch.ethz.idsc.retina.util.math;

import junit.framework.TestCase;

public class TruncatedGaussianTest extends TestCase {
  public void testSimple() {
    double tg = new TruncatedGaussian(10, .5, 9.9, 10.3).nextValue();
    assertTrue(9.9 <= tg);
    assertTrue(tg <= 10.3);
  }
}
