// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.ProviderRank;
import ch.ethz.idsc.gokart.core.ProviderRanks;
import junit.framework.TestCase;

public class ProviderRanksTest extends TestCase {
  public void testSimple() {
    for (ProviderRank providerRank : ProviderRank.values())
      assertTrue(Objects.nonNull(ProviderRanks.color(providerRank)));
  }
}
