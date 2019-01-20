// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class VectorSignalTest extends TestCase {
  public void testSimple() {
    VectorSignal vectorSignal = new VectorSignal(Tensors.vector(3, 5, 8), RealScalar.of(4.2));
    assertEquals(vectorSignal.apply(RealScalar.of(1.2)), RealScalar.of(3));
    assertEquals(vectorSignal.apply(RealScalar.of(-1.2)), RealScalar.of(8));
  }
}
