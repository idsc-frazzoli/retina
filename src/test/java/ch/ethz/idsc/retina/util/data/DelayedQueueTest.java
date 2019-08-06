// code by jph
package ch.ethz.idsc.retina.util.data;

import java.util.Optional;

import junit.framework.TestCase;

public class DelayedQueueTest extends TestCase {
  public void testSimple() {
    DelayedQueue<Integer> delayedQueue = new DelayedQueue<>(3);
    assertFalse(delayedQueue.push(100).isPresent());
    assertFalse(delayedQueue.push(101).isPresent());
    assertFalse(delayedQueue.push(102).isPresent());
    {
      Optional<Integer> optional = delayedQueue.push(103);
      assertEquals(optional.get().intValue(), 100);
    }
    {
      Optional<Integer> optional = delayedQueue.push(104);
      assertEquals(optional.get().intValue(), 101);
    }
    {
      Optional<Integer> optional = delayedQueue.push(105);
      assertEquals(optional.get().intValue(), 102);
    }
    {
      Optional<Integer> optional = delayedQueue.push(106);
      assertEquals(optional.get().intValue(), 103);
    }
  }

  public void testSingleton() {
    DelayedQueue<Integer> delayedQueue = new DelayedQueue<>(0);
    {
      Optional<Integer> optional = delayedQueue.push(103);
      assertEquals(optional.get().intValue(), 103);
    }
  }

  public void testFail() {
    try {
      new DelayedQueue<>(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
