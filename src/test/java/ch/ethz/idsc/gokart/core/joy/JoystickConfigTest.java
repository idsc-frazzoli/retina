// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class JoystickConfigTest extends TestCase {
  public void testSimple() {
    JoystickConfig.GLOBAL.createProvider();
  }

  public void testTorqueLimit() {
    Scalar scalar = JoystickConfig.GLOBAL.torqueLimit;
    Sign.requirePositive(scalar);
    Clip clip = Clip.function(Quantity.of(500, NonSI.ARMS), Quantity.of(2315, NonSI.ARMS));
    clip.requireInside(scalar);
    ExactScalarQ.require(scalar);
  }

  public void testTorqueLimitClip() {
    Clip clip = JoystickConfig.GLOBAL.torqueLimitClip();
    clip.requireInside(Quantity.of(+234, NonSI.ARMS));
    clip.requireInside(Quantity.of(-198, NonSI.ARMS));
  }
}
