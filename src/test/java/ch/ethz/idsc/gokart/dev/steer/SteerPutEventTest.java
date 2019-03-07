// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerPutEventTest extends TestCase {
  public void testSimple() {
    Scalar torque = Quantity.of(1.2, "SCT");
    SteerPutEvent steerPutEvent = SteerPutEvent.createOn(torque);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerPutEvent.insert(byteBuffer);
    assertEquals(byteBuffer.get(0), 1);
    assertEquals(byteBuffer.getFloat(1), 1.2f);
    byteBuffer.position(0);
    SteerPutEvent spe2 = SteerPutEvent.from(byteBuffer);
    assertEquals(spe2.getTorque().number().floatValue(), torque.number().floatValue());
    assertEquals(steerPutEvent.asVector(), spe2.asVector());
  }

  public void testCreate() {
    SteerPutEvent steerPutEvent = //
        SteerPutEvent.create(Word.createByte("not important", (byte) 0), Quantity.of(-0.9, "SCT"));
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerPutEvent.insert(byteBuffer);
    assertEquals(byteBuffer.get(0), 0);
    assertEquals(byteBuffer.getFloat(1), -0.9f);
    assertTrue(Scalars.isZero(steerPutEvent.asVector().Get(0)));
  }

  public void testMotTrq() {
    assertTrue(Arrays.equals(SteerPutEvent.PASSIVE_MOT_TRQ_0.asArray(), new byte[5]));
    assertTrue(Arrays.equals(SteerPutEvent.PASSIVE_MOT_TRQ_1.asArray(), new byte[] { 1, 0, 0, 0, 0 }));
  }
}
