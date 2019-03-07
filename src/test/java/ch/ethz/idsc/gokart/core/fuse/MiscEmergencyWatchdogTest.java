// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEventSimulator;
import junit.framework.TestCase;

public class MiscEmergencyWatchdogTest extends TestCase {
  public void testNominal() throws Exception {
    MiscEmergencyWatchdog miscEmergencyModule = new MiscEmergencyWatchdog();
    miscEmergencyModule.first();
    assertTrue(miscEmergencyModule.putEvent().isPresent()); // default block
    miscEmergencyModule.getEvent(MiscGetEventSimulator.create((byte) 0, 1.0f));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.create((byte) 1, 0.9f));
    assertTrue(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.create((byte) 0, 1.0f));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }

  public void testEmergency() throws Exception {
    MiscEmergencyWatchdog miscEmergencyModule = new MiscEmergencyWatchdog();
    for (int index = 0; index < 10; ++index) {
      miscEmergencyModule.first();
      miscEmergencyModule.last();
    }
  }
}
