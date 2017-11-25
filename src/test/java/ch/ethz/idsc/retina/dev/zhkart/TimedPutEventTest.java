// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class TimedPutEventTest extends TestCase {
  public void testComparable() {
    TimedPutEvent<RimoPutEvent> tpe = new TimedPutEvent<>(12332, RimoPutEvent.STOP);
    assertTrue(tpe instanceof Comparable);
  }
}
