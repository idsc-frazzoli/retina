// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import junit.framework.TestCase;

public class PurePursuitSteerTest extends TestCase {
  public void testRanks() {
    assertEquals(new PurePursuitSteer().getProviderRank(), ProviderRank.AUTONOMOUS);
    assertEquals(new PurePursuitRimo().getProviderRank(), ProviderRank.AUTONOMOUS);
  }
}
