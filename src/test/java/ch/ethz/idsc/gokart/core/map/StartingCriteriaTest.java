// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class StartingCriteriaTest extends TestCase {
  public void testSimple() {
    Tensor newPos = Tensors.vector(20, 20).multiply(Quantity.of(1, SI.METER));
    Tensor oldPos = Tensors.vector(50, 40).multiply(Quantity.of(1, SI.METER));
    assertTrue(StartingCriteria.getLineTrigger(newPos, oldPos));
    assertTrue(!StartingCriteria.getLineTrigger(newPos, newPos));
    assertTrue(!StartingCriteria.getLineTrigger(oldPos, oldPos));
    assertTrue(!StartingCriteria.getLineTrigger(oldPos, newPos));
  }
}
