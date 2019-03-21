// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.ethz.idsc.gokart.core.PutProviderComparator;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class DavisImuTrackerModuleTest extends TestCase {
  public void testSimple() throws Exception {
    DavisImuTrackerModule davisImuWatchdog = new DavisImuTrackerModule();
    davisImuWatchdog.first();
    {
      Optional<RimoPutEvent> optional = davisImuWatchdog.putEvent();
      assertTrue(optional.isPresent()); // no control is issued
    }
    Thread.sleep(100); // sleep for 200[ms]
    {
      Optional<RimoPutEvent> optional = davisImuWatchdog.putEvent();
      assertTrue(optional.isPresent()); // no control is issued
    }
    Thread.sleep(100); // sleep for 200[ms]
    {
      Optional<RimoPutEvent> optional = davisImuWatchdog.putEvent();
      assertTrue(optional.isPresent()); // no control is issued
    }
    // TODO JPH feed with imu and then check
    davisImuWatchdog.last();
  }

  public void testRank() {
    DavisImuTrackerModule davisImuWatchdog = new DavisImuTrackerModule();
    assertEquals(davisImuWatchdog.getProviderRank(), ProviderRank.EMERGENCY);
  }

  public void testAddSingle() {
    EmergencyModule<RimoPutEvent> rpp1 = new DavisImuTrackerModule();
    Set<EmergencyModule<RimoPutEvent>> providers = //
        new ConcurrentSkipListSet<>(PutProviderComparator.INSTANCE);
    providers.add(rpp1);
    providers.add(rpp1);
    providers.add(rpp1);
    assertEquals(providers.size(), 1);
  }
}
