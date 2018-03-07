// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Objects;

import ch.ethz.idsc.owl.math.state.ProviderRank;
import junit.framework.TestCase;

public class ProviderRanksTest extends TestCase {
  public void testSimple() {
    for (ProviderRank providerRank : ProviderRank.values())
      assertTrue(Objects.nonNull(ProviderRanks.color(providerRank)));
  }
}
