// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetEventSimulator;
import junit.framework.TestCase;

public class MiscEmergencyModuleTest extends TestCase {
  public void testNominal() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    MiscGetEvent miscGetEvent = MiscGetEventSimulator.create((byte) 0, 1.0f);
    miscEmergencyModule.getEvent(miscGetEvent);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }

  public void testEmergency() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    MiscGetEvent miscGetEvent = MiscGetEventSimulator.create((byte) 1, 0.9f);
    miscEmergencyModule.getEvent(miscGetEvent);
    assertTrue(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }

  public void testDelay() throws Exception {
    // TODO this test is insufficient
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    MiscGetEvent miscGetEvent = MiscGetEventSimulator.create((byte) 0, 0.8f); // 11.200000166893005[V]
    System.out.println(miscGetEvent.getSteerBatteryVoltage());
    miscEmergencyModule.getEvent(miscGetEvent);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    Thread.sleep(1100); // timeout increased to 1[s]
    assertTrue(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }
}
