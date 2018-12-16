// code by jph
package ch.ethz.idsc.retina.util.time;

import junit.framework.TestCase;

public class SystemTimestampTest extends TestCase {
  public void testSimple() {
    String string = SystemTimestamp.asString();
    assertEquals(string.length(), 8 + 1 + 6);
  }
}
