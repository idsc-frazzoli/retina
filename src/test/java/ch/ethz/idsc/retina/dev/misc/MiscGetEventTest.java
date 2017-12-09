// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MiscGetEventTest extends TestCase {
  public void testNoEmergency() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.put((byte) 0);
    byteBuffer.putFloat(1.0f);
    byteBuffer.flip();
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    assertEquals(miscGetEvent.getSteerBatteryVoltage(), Quantity.of(14, "V"));
    assertFalse(miscGetEvent.isEmergency());
    assertFalse(miscGetEvent.isCommTimeout());
    assertEquals(miscGetEvent.length(), 5);
  }

  public void testCommTimeout() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.put((byte) 1);
    byteBuffer.putFloat(0.5f);
    byteBuffer.flip();
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    // System.out.println(miscGetEvent.getSteerBatteryVoltage());
    assertEquals(miscGetEvent.getSteerBatteryVoltage(), Quantity.of(7, "V"));
    assertTrue(miscGetEvent.isEmergency());
    assertTrue(miscGetEvent.isCommTimeout());
    assertEquals(miscGetEvent.length(), 5);
  }
}
