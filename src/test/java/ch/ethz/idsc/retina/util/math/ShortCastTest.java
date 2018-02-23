// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ShortCastTest extends TestCase {
  public void testSimple() {
    short value = -3;
    Tensor vector = Tensors.vector(value);
    assertEquals(vector.toString(), "{-3}");
  }
}
