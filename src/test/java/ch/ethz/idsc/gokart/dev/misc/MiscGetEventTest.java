// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MiscGetEventTest extends TestCase {
  public void testNoEmergency() {
    MiscGetEvent miscGetEvent = MiscGetEventSimulator.createVoltage(14f);
    assertEquals(miscGetEvent.getSteerBatteryVoltage(), Quantity.of(14, "V"));
    assertFalse(miscGetEvent.isEmergency());
    assertFalse(miscGetEvent.isCommTimeout());
    assertEquals(miscGetEvent.length(), 5);
  }

  public void testCommTimeout() {
    MiscGetEvent miscGetEvent = MiscGetEventSimulator.create((byte) 1, 0.5f);
    assertEquals(miscGetEvent.getSteerBatteryVoltage(), Quantity.of(7, "V"));
    assertTrue(miscGetEvent.isEmergency());
    assertTrue(miscGetEvent.isCommTimeout());
    assertEquals(miscGetEvent.length(), 5);
  }
}
