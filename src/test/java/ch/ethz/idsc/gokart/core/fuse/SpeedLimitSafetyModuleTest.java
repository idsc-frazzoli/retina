// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import junit.framework.TestCase;

public class SpeedLimitSafetyModuleTest extends TestCase {
  public void testSimple() throws Exception {
    SpeedLimitSafetyModule speedLimitSafetyModule = new SpeedLimitSafetyModule();
    String name = speedLimitSafetyModule.getClass().getSimpleName();
    assertEquals(name, "SpeedLimitSafetyModule");
    speedLimitSafetyModule.first();
    assertFalse(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(10, -20)); // slow
    assertFalse(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(6000, 0)); // 6000 -> 100.0[rad*s^-1]
    assertTrue(speedLimitSafetyModule.putEvent().isPresent());
    assertEquals(speedLimitSafetyModule.putEvent(), StaticHelper.OPTIONAL_RIMO_PASSIVE);
    Thread.sleep(600);
    assertFalse(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(10, -20)); // slow
    assertFalse(speedLimitSafetyModule.putEvent().isPresent());
    speedLimitSafetyModule.getEvent(RimoGetEvents.create(0, -6000)); // 6000 -> 100.0[rad*s^-1]
    assertTrue(speedLimitSafetyModule.putEvent().isPresent());
    assertEquals(speedLimitSafetyModule.putEvent(), StaticHelper.OPTIONAL_RIMO_PASSIVE);
    speedLimitSafetyModule.last();
  }
}
