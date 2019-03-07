// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class SteerColumnAdapterTest extends TestCase {
  public void testSimple() {
    try {
      new SteerColumnAdapter(false, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
