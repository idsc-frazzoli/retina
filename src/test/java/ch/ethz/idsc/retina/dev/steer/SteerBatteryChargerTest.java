// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.retina.dev.misc.MiscGetEventSimulator;
import junit.framework.TestCase;

public class SteerBatteryChargerTest extends TestCase {
  public void testSimple() {
    assertTrue(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
    assertEquals(SteerBatteryCharger.INSTANCE.putEvent().get(), SteerPutEvent.PASSIVE_MOT_TRQ_0);
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.createVoltage(7));
    assertFalse(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.createVoltage(14));
    assertEquals(SteerBatteryCharger.INSTANCE.putEvent().get(), SteerPutEvent.PASSIVE_MOT_TRQ_0);
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.createVoltage(12.5));
    assertFalse(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
    SteerBatteryCharger.INSTANCE.getEvent(MiscGetEventSimulator.createVoltage(13.5));
    assertTrue(SteerBatteryCharger.INSTANCE.putEvent().isPresent());
  }
}
