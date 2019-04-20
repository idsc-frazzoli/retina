// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import junit.framework.TestCase;

public class Vmu931_DPSTest extends TestCase {
  public void testIsActive() {
    assertTrue(Vmu931_DPS._250.isActive((byte) 16));
    assertFalse(Vmu931_DPS._250.isActive((byte) 32));
    assertFalse(Vmu931_DPS._250.isActive((byte) 64));
    assertFalse(Vmu931_DPS._250.isActive((byte) 128));
  }

  public void testSetActive() {
    byte[] data = Vmu931_DPS._250.setActive();
    assertEquals(data[0], 118);
    assertEquals(data[1], 97);
    assertEquals(data[2], 114);
    assertEquals(data[3], 48);
    assertEquals(data.length, 4);
  }
}
