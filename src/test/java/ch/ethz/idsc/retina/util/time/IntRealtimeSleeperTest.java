// code by jph
package ch.ethz.idsc.retina.util.time;

import junit.framework.TestCase;

public class IntRealtimeSleeperTest extends TestCase {
  public void testOverflow() {
    IntRealtimeSleeper intRealtimeSleeper = new IntRealtimeSleeper(1.0);
    intRealtimeSleeper.now(Integer.MAX_VALUE);
    intRealtimeSleeper.now(Integer.MIN_VALUE);
    assertTrue(true);
  }
}
