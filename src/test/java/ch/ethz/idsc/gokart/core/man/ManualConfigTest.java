// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ManualConfigTest extends TestCase {
  public void testSimple() {
    ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
    assertNotNull(manualControlProvider);
  }

  public void testTorqueLimit() {
    Scalar scalar = ManualConfig.GLOBAL.torqueLimit;
    Sign.requirePositive(scalar);
    Clip clip = Clip.function(Quantity.of(500, NonSI.ARMS), Quantity.of(2315, NonSI.ARMS));
    clip.requireInside(scalar);
    ExactScalarQ.require(scalar);
  }

  public void testTorqueLimitClip() {
    Clip clip = ManualConfig.GLOBAL.torqueLimitClip();
    clip.requireInside(Quantity.of(+234, NonSI.ARMS));
    clip.requireInside(Quantity.of(-198, NonSI.ARMS));
  }
}
