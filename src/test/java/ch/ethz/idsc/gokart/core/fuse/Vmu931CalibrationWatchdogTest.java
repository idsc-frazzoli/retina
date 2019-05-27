// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Vmu931CalibrationWatchdogTest extends TestCase {
  public void testNonAvailable() {
    Vmu931CalibrationWatchdog vmu931CalibrationWatchdog = new Vmu931CalibrationWatchdog();
    vmu931CalibrationWatchdog.first();
    Optional<RimoPutEvent> optional = vmu931CalibrationWatchdog.putEvent();
    assertTrue(optional.isPresent());
    assertTrue(Chop.NONE.allZero(optional.get().getTorque_Y_pair()));
    vmu931CalibrationWatchdog.last();
  }
  // public void testOk() throws InterruptedException {
  // Vmu931CalibrationWatchdog vmu931CalibrationWatchdog = new Vmu931CalibrationWatchdog(() -> true);
  // vmu931CalibrationWatchdog.first();
  // assertTrue(vmu931CalibrationWatchdog.putEvent().isPresent());
  // vmu931CalibrationWatchdog.vmu931ImuFrame(null);
  // assertFalse(vmu931CalibrationWatchdog.putEvent().isPresent());
  // Thread.sleep(500);
  // assertTrue(vmu931CalibrationWatchdog.putEvent().isPresent());
  // vmu931CalibrationWatchdog.last();
  // }
}
