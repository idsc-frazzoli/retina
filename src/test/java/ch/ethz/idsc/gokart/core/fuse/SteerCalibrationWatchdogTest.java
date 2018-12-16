// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetHelper;
import junit.framework.TestCase;

public class SteerCalibrationWatchdogTest extends TestCase {
  public void testSimple() throws Exception {
    SteerCalibrationWatchdog steerCalibrationWatchdog = new SteerCalibrationWatchdog();
    steerCalibrationWatchdog.first();
    assertTrue(steerCalibrationWatchdog.putEvent().isPresent());
    steerCalibrationWatchdog.last();
  }

  public void testNonPresent() throws Exception {
    SteerColumnTracker steerColumnTracker = new SteerColumnTracker();
    steerColumnTracker.getEvent(SteerGetHelper.create(+0.75f));
    steerColumnTracker.getEvent(SteerGetHelper.create(-0.75f));
    assertTrue(steerColumnTracker.isSteerColumnCalibrated());
    Optional<RimoPutEvent> optional = SteerCalibrationWatchdog.create(steerColumnTracker);
    assertFalse(optional.isPresent());
  }
}
