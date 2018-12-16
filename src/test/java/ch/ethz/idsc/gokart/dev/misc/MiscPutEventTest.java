// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.nio.ByteBuffer;
import java.util.Arrays;

import junit.framework.TestCase;

public class MiscPutEventTest extends TestCase {
  public void testSimple() {
    assertEquals(MiscPutEvent.FALLBACK.resetConnection, 0);
    assertEquals(MiscPutEvent.FALLBACK.length(), 6);
  }

  public void testResetcon() {
    assertEquals(MiscPutEvent.RESETCON.resetConnection, 1);
  }

  public void testBytebuffer() {
    MiscPutEvent miscPutEvent = MiscPutEvent.RESETCON;
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[miscPutEvent.length()]);
    miscPutEvent.insert(byteBuffer);
    byteBuffer.position(0);
    MiscPutEvent miscPutClone = new MiscPutEvent(byteBuffer);
    assertEquals(miscPutEvent.asVector(), miscPutClone.asVector());
    assertTrue(Arrays.equals(miscPutEvent.asArray(), miscPutClone.asArray()));
  }
}
