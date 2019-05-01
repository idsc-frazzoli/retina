// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PoseHelperTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor state = PoseHelper.attachUnits(vector);
    assertEquals(vector, PoseHelper.toUnitless(state));
  }

  public void testUnits() {
    Tensor state = Tensors.fromString("{1[m], 2[m], 3}");
    Tensor vector = PoseHelper.toUnitless(state);
    assertEquals(state, PoseHelper.attachUnits(vector));
  }
}
