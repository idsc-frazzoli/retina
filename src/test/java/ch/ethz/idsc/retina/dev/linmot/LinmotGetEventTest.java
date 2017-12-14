// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LinmotGetEventTest extends TestCase {
  public void testLength() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort((short) 0x4c37);
    byteBuffer.putShort((short) 0x08c1);
    byteBuffer.putInt(10_000_000);
    byteBuffer.putInt(7000);
    byteBuffer.putShort((short) 500);
    byteBuffer.putShort((short) 200);
    byteBuffer.flip();
    LinmotGetEvent linmotGetEvent = LinmotSocket.INSTANCE.createGetEvent(byteBuffer);
    assertEquals(linmotGetEvent.length(), 16);
    linmotGetEvent.asArray();
    assertTrue(Objects.nonNull(linmotGetEvent.toInfoString()));
    assertEquals(linmotGetEvent.getActualPosition(), Quantity.of(1, "m"));
    assertEquals(linmotGetEvent.getWindingTemperature1(), Quantity.of(50, "degC"));
    assertEquals(linmotGetEvent.getWindingTemperature2(), Quantity.of(20, "degC"));
    assertTrue(linmotGetEvent.isOperational());
    assertTrue(LinmotConfig.GLOBAL.isTemperatureOperationSafe(linmotGetEvent));
    assertEquals(linmotGetEvent.getWindingTemperatureMax(), Quantity.of(50, "degC"));
  }
}
