// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import junit.framework.TestCase;

public class LinmotJoystickModuleTest extends TestCase {
  public void testSimple() {
    LinmotJoystickModule linmotJoystickModule = new LinmotJoystickModule();
    Optional<LinmotPutEvent> optional = linmotJoystickModule.putEvent();
    assertFalse(optional.isPresent());
    assertFalse(linmotJoystickModule.putEvent().isPresent());
    assertEquals(linmotJoystickModule.getProviderRank(), ProviderRank.MANUAL);
  }
}
