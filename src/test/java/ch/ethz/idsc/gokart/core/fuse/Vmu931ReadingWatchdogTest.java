// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Vmu931ReadingWatchdogTest extends TestCase {
  public void testNonAvailable() {
    Vmu931ReadingWatchdog vmu931ReadingWatchdog = new Vmu931ReadingWatchdog();
    vmu931ReadingWatchdog.first();
    Optional<RimoPutEvent> optional = vmu931ReadingWatchdog.putEvent();
    assertTrue(optional.isPresent());
    assertTrue(Chop.NONE.allZero(optional.get().getTorque_Y_pair()));
    vmu931ReadingWatchdog.last();
  }
}
