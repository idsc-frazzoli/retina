// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscGetEventSimulator;
import junit.framework.TestCase;

public class SteerBatteryWatchdogTest extends TestCase {
  public void testNominal() throws Exception {
    SteerBatteryWatchdog steerBatteryWatchdog = new SteerBatteryWatchdog();
    steerBatteryWatchdog.first();
    MiscGetEvent miscGetEvent = MiscGetEventSimulator.create((byte) 0, 1.0f);
    steerBatteryWatchdog.getEvent(miscGetEvent);
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.last();
  }

  public void testTimeout() throws Exception {
    SteerBatteryWatchdog steerBatteryWatchdog = new SteerBatteryWatchdog();
    steerBatteryWatchdog.first();
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(10.2));
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    Thread.sleep(1050);
    assertTrue(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.last();
  }

  public void testPacify() throws Exception {
    SteerBatteryWatchdog steerBatteryWatchdog = new SteerBatteryWatchdog();
    steerBatteryWatchdog.first();
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(10.2));
    Thread.sleep(100);
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    Thread.sleep(100);
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(10.2));
    Thread.sleep(100);
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.getEvent(MiscGetEventSimulator.createVoltage(11.2));
    assertFalse(steerBatteryWatchdog.putEvent().isPresent());
    steerBatteryWatchdog.last();
  }
}
