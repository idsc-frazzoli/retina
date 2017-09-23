// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.PriorityQueue;

import junit.framework.TestCase;

public class ProviderRankTest extends TestCase {
  public void testSimple() {
    PriorityQueue<ProviderRank> pr = new PriorityQueue<>();
    pr.add(ProviderRank.EMERGENCY);
    pr.add(ProviderRank.FALLBACK);
    pr.add(ProviderRank.GODMODE);
    System.out.println(pr);
  }
}
