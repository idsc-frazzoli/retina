// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ClothoidPursuitConfigTest extends TestCase {
  public void testSimple() {
    ClothoidPursuitConfig clothoidPursuitConfig = ClothoidPursuitConfig.GLOBAL;
    Clip clip = clothoidPursuitConfig.ratioLimits();
    clip.requireInside(Quantity.of(-0.1, "m^-1"));
    clip.requireInside(Quantity.of(+0.1, "m^-1"));
  }
}
