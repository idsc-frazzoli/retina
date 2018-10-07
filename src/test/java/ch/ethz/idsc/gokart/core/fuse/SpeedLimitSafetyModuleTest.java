// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
import junit.framework.TestCase;

public class SpeedLimitSafetyModuleTest extends TestCase {
  public void testSimple() throws Exception {
    SpeedLimitSafetyModule speedLimitSafetyModule = new SpeedLimitSafetyModule();
    speedLimitSafetyModule.first();
    assertTrue(speedLimitSafetyModule.putEvent().isPresent());
    assertEquals(speedLimitSafetyModule.putEvent(), StaticHelper.OPTIONAL_RIMO_PASSIVE);
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(10, -20)); // slow
    assertFalse(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(6000, 0)); // 6000 -> 100.0[rad*s^-1]
    assertTrue(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(10, -20)); // slow
    assertFalse(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(0, -6000)); // 6000 -> 100.0[rad*s^-1]
    assertTrue(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.last();
  }
}
