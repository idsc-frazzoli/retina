// code by jph
package ch.ethz.idsc.gokart.core.fuse;

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

  public void testTimeout() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(10.2));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    Thread.sleep(1050);
    assertTrue(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }

  public void testPacify() throws Exception {
    MiscEmergencyModule miscEmergencyModule = new MiscEmergencyModule();
    miscEmergencyModule.first();
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(10.2));
    Thread.sleep(100);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    Thread.sleep(100);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(10.2));
    Thread.sleep(100);
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(miscEmergencyModule.putEvent().isPresent());
    miscEmergencyModule.last();
  }
}
