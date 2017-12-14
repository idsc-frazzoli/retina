// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class JoystickConfigTest extends TestCase {
  public void testSimple() {
    Clip clip = Clip.function(1, 3);
    clip.isInsideElseThrow(JoystickConfig.GLOBAL.deadManPeriodSeconds());
  }
}
