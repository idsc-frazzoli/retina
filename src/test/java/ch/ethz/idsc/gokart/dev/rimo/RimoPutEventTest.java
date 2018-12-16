// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Tensors;
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
    assertEquals(rpe.getTorque_Y_pair(), Tensors.fromString("{-1[ARMS], 2[ARMS]}"));
  }

  public void testTorquePair() {
    RimoPutTire putL = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 10);
    RimoPutTire putR = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 20);
    RimoPutEvent rpe = new RimoPutEvent(putL, putR);
    assertEquals(rpe.getTorque_Y_pair(), Tensors.fromString("{-10[ARMS], 20[ARMS]}"));
  }
}
