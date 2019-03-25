// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DubendorfTrackTest extends TestCase {
  public void testConstantRadius() {
    Tensor tensor = DubendorfTrack.getConstantRadius(Quantity.of(3, SI.METER), 5);
    assertEquals(tensor, Tensors.fromString("{3[m], 3[m], 3[m], 3[m], 3[m]}"));
  }
}
