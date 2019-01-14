// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class TruncatedGaussianTest extends TestCase {
  public void testSimple() {
    TruncatedGaussian truncatedGaussian = new TruncatedGaussian(10, .5, 9.9, 10.3);
    Set<Double> set = new HashSet<>();
    for (int count = 0; count < 100; ++count) {
      double value = truncatedGaussian.nextValue();
      set.add(value);
      assertTrue(9.9 <= value);
      assertTrue(value <= 10.3);
    }
    assertTrue(95 < set.size());
  }

  public void testFail() {
    try {
      new TruncatedGaussian(10, .5, 9.9, 9.8);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
