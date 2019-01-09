// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TorqueVectoringHelperTest extends TestCase {
  public void testClip1() {
    Tensor tensor = TorqueVectoringHelper.clip(RealScalar.of(1.25), RealScalar.ZERO);
    assertEquals(tensor, Tensors.vector(1, 0.25));
  }

  public void testClip2() {
    Tensor tensor = TorqueVectoringHelper.clip(RealScalar.ZERO, RealScalar.of(1.25));
    assertEquals(tensor, Tensors.vector(0.25, 1));
  }
}
