// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class VelocityHelperTest extends TestCase {
  public void testToUnitless() {
    Tensor tensor = VelocityHelper.toUnitless(Tensors.fromString("{1[m*s^-1], 2[m*s^-1], 3[s^-1]}"));
    assertEquals(tensor, Tensors.vector(1, 2, 3));
  }

  public void testAttachUnits() {
    Tensor velocity = VelocityHelper.attachUnits(Tensors.vector(2, 3, 4));
    assertEquals(velocity, Tensors.fromString("{2[m*s^-1], 3[m*s^-1], 4[s^-1]}"));
    Tensor tensor = VelocityHelper.toUnitless(velocity);
    assertEquals(tensor, Tensors.vector(2, 3, 4));
  }

  public void testZero() {
    assertEquals(VelocityHelper.ZERO.toString(), "{0.0[m*s^-1], 0.0[m*s^-1], 0.0[s^-1]}");
  }
}
