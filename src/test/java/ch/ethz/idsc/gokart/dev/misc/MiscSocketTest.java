// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import junit.framework.TestCase;

public class MiscSocketTest extends TestCase {
  public void testSimple() {
    byte[] array = new byte[5];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put((byte) 1);
    byteBuffer.putFloat(1.234f);
    byteBuffer.flip();
    MiscGetEvent miscGetEvent = MiscSocket.INSTANCE.createGetEvent(byteBuffer);
    assertTrue(Arrays.equals(miscGetEvent.asArray(), array));
  }

  public void testPeriod() {
    assertEquals(MiscSocket.INSTANCE.getPutPeriod_ms(), 20);
  }
}
