// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class SteerGetEventTest extends TestCase {
  public void testSimple() {
    assertEquals(SteerGetEvent.LENGTH, 44);
  }

  public void testInstance() {
    byte[] array = new byte[44];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int c = 0; c < 11; ++c)
      byteBuffer.putFloat(c * 3.3f + 2.7f);
    byteBuffer.flip();
    SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
    assertFalse(steerGetEvent.isActive());
    byte[] array2 = new byte[44];
    byteBuffer = ByteBuffer.wrap(array2);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerGetEvent.insert(byteBuffer);
    assertTrue(Arrays.equals(array, array2));
    assertEquals(steerGetEvent.length(), 44);
    Tensor raw = steerGetEvent.asVector();
    assertEquals(raw.length(), 11);
  }

  public void testIsActive() {
    byte[] array = new byte[44];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int c = 0; c < 11; ++c)
      byteBuffer.putFloat(c * 3.3f + 2.7f);
    byteBuffer.flip();
    byteBuffer.putFloat(4 * 6, 2.0f);
    SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
    assertTrue(steerGetEvent.isActive());
    byte[] array2 = new byte[44];
    byteBuffer = ByteBuffer.wrap(array2);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerGetEvent.insert(byteBuffer);
    assertTrue(Arrays.equals(array, array2));
    assertEquals(steerGetEvent.length(), 44);
  }
}
