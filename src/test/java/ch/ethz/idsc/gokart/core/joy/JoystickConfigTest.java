// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class JoystickConfigTest extends TestCase {
  public void testSimple() {
    Clip clip = Clip.function(1, 3);
    clip.requireInside(JoystickConfig.GLOBAL.deadManPeriodSeconds());
  }

  public void testBrakeDuration() {
    Clip clip = Clip.function(1, 3.5);
    clip.requireInside(JoystickConfig.GLOBAL.brakeDurationSeconds());
  }

  public void testSafeSpeed() {
    assertTrue(JoystickConfig.GLOBAL.isSpeedSlow(Tensors.fromString("{0[rad*s^-1],0[rad*s^-1]}")));
    assertTrue(JoystickConfig.GLOBAL.isSpeedSlow(Tensors.fromString("{0[rad*s^-1],1[rad*s^-1]}")));
    assertFalse(JoystickConfig.GLOBAL.isSpeedSlow(Tensors.fromString("{0[rad*s^-1],8[rad*s^-1]}")));
    assertFalse(JoystickConfig.GLOBAL.isSpeedSlow(Tensors.fromString("{8[rad*s^-1],0[rad*s^-1]}")));
    assertFalse(JoystickConfig.GLOBAL.isSpeedSlow(Tensors.fromString("{0[rad*s^-1],-8[rad*s^-1]}")));
    assertFalse(JoystickConfig.GLOBAL.isSpeedSlow(Tensors.fromString("{-8[rad*s^-1],0[rad*s^-1]}")));
  }
}
