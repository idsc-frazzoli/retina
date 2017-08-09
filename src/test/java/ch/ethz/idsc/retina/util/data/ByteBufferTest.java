// code by jph
package ch.ethz.idsc.retina.util.data;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class ByteBufferTest extends TestCase {
  public void testPosition() {
    byte[] data = new byte[20];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    assertTrue(byteBuffer.position() == 0);
    byteBuffer.getInt(5);
    assertTrue(byteBuffer.position() == 0);
  }
}
