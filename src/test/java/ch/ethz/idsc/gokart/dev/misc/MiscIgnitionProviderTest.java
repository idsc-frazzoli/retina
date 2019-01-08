// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class MiscIgnitionProviderTest extends TestCase {
  public void testSimple() throws InterruptedException {
    MiscIgnitionProvider miscIgnitionProvider = MiscIgnitionProvider.INSTANCE;
    assertEquals(miscIgnitionProvider.getProviderRank(), ProviderRank.CALIBRATION);
    assertTrue(miscIgnitionProvider.isIdle());
    assertFalse(miscIgnitionProvider.putEvent().isPresent());
    miscIgnitionProvider.protected_schedule();
    Optional<MiscPutEvent> optional = miscIgnitionProvider.putEvent();
    assertTrue(optional.isPresent());
    MiscPutEvent mpe = optional.get();
    assertEquals(mpe.resetConnection, 1);
    Thread.sleep(270); // duration of schedule is 250[ms]
    assertFalse(miscIgnitionProvider.putEvent().isPresent());
  }
}
