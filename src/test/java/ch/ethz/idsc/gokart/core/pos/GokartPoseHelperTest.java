// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartPoseHelperTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor state = GokartPoseHelper.attachUnits(vector);
    assertEquals(vector, GokartPoseHelper.toUnitless(state));
  }

  public void testUnits() {
    Tensor state = Tensors.fromString("{1[m], 2[m], 3}");
    Tensor vector = GokartPoseHelper.toUnitless(state);
    assertEquals(state, GokartPoseHelper.attachUnits(vector));
  }
}
