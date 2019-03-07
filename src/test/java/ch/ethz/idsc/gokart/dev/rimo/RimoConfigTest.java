// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoConfigTest extends TestCase {
  public void testSimple() {
    RimoConfig rimoConfig = new RimoConfig();
    Chop chop = rimoConfig.speedChop();
    assertEquals(chop.apply(Quantity.of(+0.01, SI.VELOCITY)), Quantity.of(0, SI.VELOCITY));
    assertEquals(chop.apply(Quantity.of(-0.01, SI.VELOCITY)), Quantity.of(0, SI.VELOCITY));
    assertEquals(chop.apply(Quantity.of(+0.05, SI.VELOCITY)), Quantity.of(+0.05, SI.VELOCITY));
    assertEquals(chop.apply(Quantity.of(-0.05, SI.VELOCITY)), Quantity.of(-0.05, SI.VELOCITY));
  }
}
