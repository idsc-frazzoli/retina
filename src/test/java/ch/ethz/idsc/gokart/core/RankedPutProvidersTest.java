// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.List;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class RankedPutProvidersTest extends TestCase {
  public void testSimple() {
    RankedPutProviders<RimoPutEvent> rankedPutProviders = new RankedPutProviders<>();
    assertEquals(rankedPutProviders.size(), 0);
    for (List<PutProvider<RimoPutEvent>> list : rankedPutProviders.values())
      assertTrue(list.isEmpty());
  }
}
