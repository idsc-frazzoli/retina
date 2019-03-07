// code by jph
package ch.ethz.idsc.retina.davis._240c;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Davis240cDecoderTest extends TestCase {
  public void testSimple() {
    Tensor t1 = Tensors.empty();
    Tensor t2 = Tensors.vector(1, 2, 3);
    assertTrue(t1.getClass() == t2.getClass()); // TensorImpl
    // TensorImpl != Tensor.class
  }

  public void testMore() {
    Davis240cDecoder d1 = new Davis240cDecoder();
    assertTrue(d1.getClass() == Davis240cDecoder.class);
  }
}
