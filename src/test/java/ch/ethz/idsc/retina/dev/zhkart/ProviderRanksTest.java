// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Objects;

import junit.framework.TestCase;

public class ProviderRanksTest extends TestCase {
  public void testSimple() {
    for (ProviderRank providerRank : ProviderRank.values())
      assertTrue(Objects.nonNull(ProviderRanks.color(providerRank)));
  }
}
