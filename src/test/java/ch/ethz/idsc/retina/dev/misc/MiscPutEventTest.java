// code by jph
package ch.ethz.idsc.retina.dev.misc;

import junit.framework.TestCase;

public class MiscPutEventTest extends TestCase {
  public void testSimple() {
    assertEquals(MiscPutEvent.PASSIVE.length(), 6);
  }
}
