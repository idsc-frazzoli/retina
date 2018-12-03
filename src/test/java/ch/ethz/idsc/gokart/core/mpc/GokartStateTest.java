// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class GokartStateTest extends TestCase {
  public void testLength() {
    GokartState gokartState = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    assertEquals(gokartState.length(), 44);
  }

  public void testSerialization() {
    GokartState gokartState = new GokartState(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    byte[] array = new byte[gokartState.length()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartState.insert(byteBuffer);
    byteBuffer.flip();
    GokartState gokartState2 = new GokartState(byteBuffer);
    assertEquals(gokartState.asVector(), gokartState2.asVector());
  }
}
