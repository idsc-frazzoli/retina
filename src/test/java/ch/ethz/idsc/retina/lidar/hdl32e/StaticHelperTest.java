// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertEquals(StaticHelper.signed24bit((short) 0xfff), -1);
    assertEquals(StaticHelper.signed24bit((short) 0x7ff), 0x7ff);
    assertEquals(StaticHelper.signed24bit((short) 0xf7ff), 0x7ff);
  }

  public void testDatasheet() {
    assertEquals(StaticHelper.signed24bit((short) 0x0fde), -34);
    assertEquals(StaticHelper.signed24bit((short) 0x1055), 85);
    assertEquals(StaticHelper.signed24bit((short) 0x231b), 795);
    assertEquals(StaticHelper.signed24bit((short) 0x3009), 9);
  }
}
