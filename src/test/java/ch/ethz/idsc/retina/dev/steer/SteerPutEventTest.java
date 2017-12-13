// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerPutEventTest extends TestCase {
  public void testSimple() {
    SteerPutEvent spe = SteerPutEvent.createOn(Quantity.of(1.2, "SCT"));
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    spe.insert(byteBuffer);
    assertEquals(byteBuffer.get(0), 1);
    assertEquals(byteBuffer.getFloat(1), 1.2f);
  }

  public void testCreate() {
    SteerPutEvent spe = SteerPutEvent.create(SteerPutEvent.CMD_OFF, Quantity.of(-0.9, "SCT"));
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    spe.insert(byteBuffer);
    assertEquals(byteBuffer.get(0), 0);
    assertEquals(byteBuffer.getFloat(1), -0.9f);
  }
}
