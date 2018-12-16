// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class TimedPutEventTest extends TestCase {
  public void testComparable() {
    TimedPutEvent<RimoPutEvent> tp1 = new TimedPutEvent<>(12332, () -> RimoPutEvent.PASSIVE);
    TimedPutEvent<RimoPutEvent> tp2 = new TimedPutEvent<>(12330, () -> RimoPutEvent.PASSIVE);
    assertEquals(tp1.compareTo(tp2), Integer.compare(32, 30));
  }
}
