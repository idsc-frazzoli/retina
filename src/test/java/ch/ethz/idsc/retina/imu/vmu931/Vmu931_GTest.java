// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Vmu931_GTest extends TestCase {
  public void testIsActive() {
    assertFalse(Vmu931_G._4.isActive((byte) 1));
    assertTrue(Vmu931_G._4.isActive((byte) 2));
    assertFalse(Vmu931_G._4.isActive((byte) 4));
    assertFalse(Vmu931_G._4.isActive((byte) 8));
  }

  public void testSetActive() {
    byte[] data = Vmu931_G._4.setActive();
    assertEquals(data[0], 118);
    assertEquals(data[1], 97);
    assertEquals(data[2], 114);
    assertEquals(data[3], 53);
    assertEquals(data.length, 4);
  }

  public void testClip() {
    assertEquals(Vmu931_G._2.clip().min(), Quantity.of(-2 * 9.81, SI.ACCELERATION));
    assertEquals(Vmu931_G._16.clip().max(), Quantity.of(16 * 9.81, SI.ACCELERATION));
  }
}
