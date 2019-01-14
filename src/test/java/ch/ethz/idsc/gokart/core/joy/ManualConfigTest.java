// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import junit.framework.TestCase;

public class ManualConfigTest extends TestCase {
  public void testSimple() {
    ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
    assertNotNull(manualControlProvider);
  }
}
