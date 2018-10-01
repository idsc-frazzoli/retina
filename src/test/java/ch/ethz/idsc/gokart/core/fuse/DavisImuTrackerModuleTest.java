// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class DavisImuTrackerModuleTest extends TestCase {
  public void testSimple() throws Exception {
    DavisImuTrackerModule davisImuWatchdog = new DavisImuTrackerModule();
    davisImuWatchdog.first();
    {
      Optional<RimoPutEvent> optional = davisImuWatchdog.putEvent();
      assertFalse(optional.isPresent()); // no control is issued
    }
    Thread.sleep(100); // sleep for 200[ms]
    {
      Optional<RimoPutEvent> optional = davisImuWatchdog.putEvent();
      assertFalse(optional.isPresent()); // no control is issued
    }
    Thread.sleep(500); // sleep for 200[ms]
    {
      Optional<RimoPutEvent> optional = davisImuWatchdog.putEvent();
      assertTrue(optional.isPresent()); // no control is issued
    }
    davisImuWatchdog.last();
  }

  public void testRank() {
    DavisImuTrackerModule davisImuWatchdog = new DavisImuTrackerModule();
    assertEquals(davisImuWatchdog.getProviderRank(), ProviderRank.EMERGENCY);
  }
}
