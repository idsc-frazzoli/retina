// code by jph
package ch.ethz.idsc.retina.util.math;

import junit.framework.TestCase;

public class ShortUtilsTest extends TestCase {
  public void testSimple() {
    assertEquals(ShortUtils.signed24bit((short) 0xfff), -1);
    assertEquals(ShortUtils.signed24bit((short) 0x7ff), 0x7ff);
    assertEquals(ShortUtils.signed24bit((short) 0xf7ff), 0x7ff);
  }

  public void testDatasheet() {
    assertEquals(ShortUtils.signed24bit((short) 0x0fde), -34);
    assertEquals(ShortUtils.signed24bit((short) 0x1055), 85);
    assertEquals(ShortUtils.signed24bit((short) 0x231b), 795);
    assertEquals(ShortUtils.signed24bit((short) 0x3009), 9);
  }
}
