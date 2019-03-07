// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTrackerModule;
import ch.ethz.idsc.gokart.core.fuse.EmergencyModule;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class PutProviderComparatorTest extends TestCase {
  public void testSimple() {
    EmergencyModule<RimoPutEvent> rpp1 = new DavisImuTrackerModule();
    Set<EmergencyModule<RimoPutEvent>> providers = //
        new ConcurrentSkipListSet<>(PutProviderComparator.INSTANCE);
    providers.add(rpp1);
    providers.add(rpp1);
    providers.add(rpp1);
    assertEquals(providers.size(), 1);
  }
}
