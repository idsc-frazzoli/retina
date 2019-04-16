// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartControlTest extends TestCase {
  public void testSimple() {
    assertEquals(GokartControl.LENGTH, 20);
    GokartControl gokartControl = new GokartControl(1, 2, 3, 4);
    assertEquals(gokartControl.getuL(), Quantity.of(1, SI.ACCELERATION));
    assertEquals(gokartControl.getuR(), Quantity.of(2, SI.ACCELERATION));
    assertEquals(gokartControl.getudotS(), Quantity.of(3, "SCE*s^-1"));
    assertEquals(gokartControl.getuB(), RealScalar.of(4));
    assertEquals(gokartControl.getaB(), Quantity.of(0, SI.ACCELERATION));
  }
}
