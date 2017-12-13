// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class RimoPutEventTest extends TestCase {
  public void testLength() {
    assertEquals(RimoPutEvent.LENGTH, 30);
  }

  public void testSimple() {
    RimoPutTire putL = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 1);
    RimoPutTire putR = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 2);
    RimoPutEvent rpe = new RimoPutEvent(putL, putR);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[30]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    rpe.insert(byteBuffer);
    assertEquals(byteBuffer.getShort(0), 0x0009);
    assertEquals(byteBuffer.getShort(0 + 15), 0x0009);
    assertEquals(byteBuffer.getShort(2), 0);
    assertEquals(byteBuffer.getShort(2 + 15), 0);
    assertEquals(byteBuffer.getShort(4), 1);
    assertEquals(byteBuffer.getShort(4 + 15), 2);
    assertEquals(rpe.length(), 30);
  }
}
