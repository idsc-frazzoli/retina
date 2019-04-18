// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class VelocityHelperTest extends TestCase {
  public void testSimple() {
    Tensor tensor = VelocityHelper.toUnitless(Tensors.fromString("{1[m*s^-1],2[m*s^-1],3[s^-1]}"));
    assertEquals(tensor, Tensors.vector(1, 2, 3));
  }
}
