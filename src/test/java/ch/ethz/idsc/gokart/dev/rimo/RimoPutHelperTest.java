// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class RimoPutHelperTest extends TestCase {
  public void testSimple() {
    RimoPutTire putTireL = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 1);
    RimoPutTire putTireR = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 2);
    putTireL.trigger = 5;
    putTireL.sdoCommand = 24;
    putTireL.mainIndex = 31453;
    putTireL.subIndex = -10;
    putTireL.sdoData = Integer.MIN_VALUE;
    // ---
    putTireR.trigger = 1;
    putTireR.sdoCommand = 2;
    putTireR.mainIndex = 1453;
    putTireR.subIndex = -4;
    putTireR.sdoData = Integer.MAX_VALUE;
    RimoPutEvent rimoPutEvent = new RimoPutEvent(putTireL, putTireR);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[30]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    rimoPutEvent.insert(byteBuffer);
    byteBuffer.flip();
    RimoPutEvent rimoPutEvent2 = RimoPutHelper.from(byteBuffer);
    assertEquals(rimoPutEvent.putTireL.asVector(), rimoPutEvent2.putTireL.asVector());
    assertEquals(rimoPutEvent.putTireR.asVector(), rimoPutEvent2.putTireR.asVector());
  }
}
