// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.PriorityQueue;
import java.util.Queue;

import junit.framework.TestCase;

public class ProviderRankTest extends TestCase {
  public void testSimple() {
    Queue<ProviderRank> queue = new PriorityQueue<>();
    queue.add(ProviderRank.EMERGENCY);
    queue.add(ProviderRank.FALLBACK);
    queue.add(ProviderRank.GODMODE);
    assertEquals(queue.poll(), ProviderRank.GODMODE);
  }
}
