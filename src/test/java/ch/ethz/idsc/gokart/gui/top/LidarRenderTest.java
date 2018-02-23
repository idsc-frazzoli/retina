// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.util.Iterator;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LidarRenderTest extends TestCase {
  public void testIterator() {
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5);
    Iterator<Tensor> iterator = vector.iterator();
    assertEquals(iterator.next(), RealScalar.ONE);
    vector = Tensors.vector(9, 8, 7, 6);
    assertEquals(iterator.next(), RealScalar.of(2));
  }
}
