// code by jph
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SlamWaypointTest extends TestCase {
  public void testSimple() {
    Tensor element = Tensors.fromString("{5.3[m],-10.9[m],1.34}");
    Se2CoveringGroupAction se2CoveringGroupAction = //
        new Se2CoveringGroupAction(element);
    Se2CoveringGroupAction inverseAction = se2CoveringGroupAction.inverse();
    Tensor neutral = Tensors.fromString("{0[m],0[m],0}");
    {
      Tensor tensor = inverseAction.combine(element);
      assertEquals(tensor, neutral);
    }
    {
      Tensor tensor = inverseAction.combine(neutral);
      Tensor combine = new Se2CoveringGroupAction(tensor).combine(element);
      assertEquals(combine, neutral);
    }
  }
}
