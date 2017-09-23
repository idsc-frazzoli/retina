// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import junit.framework.TestCase;

public class ProviderRankTest extends TestCase {
  public void testSimple() {
    Queue<ProviderRank> queue = new PriorityQueue<>();
    queue.add(ProviderRank.EMERGENCY);
    queue.add(ProviderRank.FALLBACK);
    queue.add(ProviderRank.GODMODE);
    // System.out.println(new ArrayList<>(queue));
    assertEquals(queue.poll(), ProviderRank.GODMODE);
  }

  public void testSet() {
    Set<Integer> csls = new ConcurrentSkipListSet<>();
    csls.add(10);
    csls.add(2);
    csls.add(5);
    csls.add(0);
    csls.add(5);
    // fails without arraylist
    assertEquals(new ArrayList<>(csls), Arrays.asList(0, 2, 5, 10));
  }
}
