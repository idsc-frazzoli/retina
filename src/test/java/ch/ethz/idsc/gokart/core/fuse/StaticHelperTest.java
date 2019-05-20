// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testTorqueZero() {
    Tensor torque_Y_pair = RimoPutEvent.OPTIONAL_RIMO_PASSIVE.get().getTorque_Y_pair();
    assertTrue(Chop.NONE.allZero(torque_Y_pair));
    assertEquals(torque_Y_pair, Tensors.fromString("{0[ARMS], 0[ARMS]}"));
  }
}
