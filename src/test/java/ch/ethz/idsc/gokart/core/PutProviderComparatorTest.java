// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.ethz.idsc.gokart.core.fuse.DavisImuWatchdog;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import junit.framework.TestCase;

public class PutProviderComparatorTest extends TestCase {
  public void testSimple() {
    RimoPutProvider rpp1 = new DavisImuWatchdog();
    Set<RimoPutProvider> providers = //
        new ConcurrentSkipListSet<>(PutProviderComparator.INSTANCE);
    providers.add(rpp1);
    providers.add(rpp1);
    providers.add(rpp1);
    assertEquals(providers.size(), 1);
  }
}
