// code by jph
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SlamConfigTest extends TestCase {
  public void testSimple() {
    SlamConfig slamConfig = new SlamConfig();
    Tensor high = slamConfig.cornerHigh();
    assertEquals(high, Tensors.fromString("{70[m], 70[m]}"));
  }
}
