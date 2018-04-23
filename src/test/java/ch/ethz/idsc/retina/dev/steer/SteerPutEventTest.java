// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
    assertEquals(steerPutEvent.values_raw(), spe2.values_raw());
  }

  public void testCreate() {
    SteerPutEvent steerPutEvent = //
        SteerPutEvent.create(SteerPutEvent.CMD_OFF, Quantity.of(-0.9, "SCT"));
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerPutEvent.insert(byteBuffer);
    assertEquals(byteBuffer.get(0), 0);
    assertEquals(byteBuffer.getFloat(1), -0.9f);
    assertTrue(Scalars.isZero(steerPutEvent.values_raw().Get(0)));
  }
}
