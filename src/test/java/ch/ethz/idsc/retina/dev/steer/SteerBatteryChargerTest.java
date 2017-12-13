// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.misc.MiscGetEventSimulator;
import junit.framework.TestCase;

public class SteerBatteryChargerTest extends TestCase {
  public void testSimple() {
    assertTrue(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
    assertEquals(SteerBatteryCharger.INSTANCE.putEvent().get(), SteerPutEvent.PASSIVE);
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.create((byte) 0, 0.5f));
    assertFalse(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.create((byte) 0, 1.0f));
    assertEquals(SteerBatteryCharger.INSTANCE.putEvent().get(), SteerPutEvent.PASSIVE);
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.create((byte) 0, 0.7f));
    assertFalse(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.create((byte) 0, 1.0f));
    assertTrue(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
  }
}
