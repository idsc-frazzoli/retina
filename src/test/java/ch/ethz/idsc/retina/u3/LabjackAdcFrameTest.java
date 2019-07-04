// code by jph
package ch.ethz.idsc.retina.u3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LabjackAdcFrameTest extends TestCase {
  public void testBPFalse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 0f, 0f });
    assertEquals(labjackAdcFrame.getADC(0), Quantity.of(0, SI.VOLT));
  }

  public void testBPTrue() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 0f, 2.4f });
    assertEquals(labjackAdcFrame.getADC(4), Quantity.of(2.4f, SI.VOLT));
  }

  public void testThrottle() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 2.5f, 0f, 0f });
    assertEquals(labjackAdcFrame.getADC(2), Quantity.of(2.5f, SI.VOLT));
  }

  public void testThrottleMax() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 5.3f, 0f, 0f });
    assertEquals(labjackAdcFrame.getADC(2), Quantity.of(5.3f, SI.VOLT));
  }

  public void testThrottleNegative() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 2.5f, 0f, 2f });
    assertEquals(labjackAdcFrame.getADC(2), Quantity.of(2.5f, SI.VOLT));
    assertEquals(labjackAdcFrame.getADC(4), Quantity.of(2f, SI.VOLT));
  }

  public void testEncoding() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 1.2f, 2.9f, 3f, 4f, -5.23f });
    byte[] array = labjackAdcFrame.asArray();
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    assertEquals(byteBuffer.getFloat(), 1.2f);
    assertEquals(byteBuffer.getFloat(), 2.9f);
    assertEquals(byteBuffer.getFloat(), 3f);
    assertEquals(byteBuffer.getFloat(), 4f);
    assertEquals(byteBuffer.getFloat(), -5.23f);
    byteBuffer.rewind();
    labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
    assertEquals(labjackAdcFrame.getADC(0), Quantity.of(1.2f, SI.VOLT));
    assertEquals(labjackAdcFrame.getADC(1), Quantity.of(2.9f, SI.VOLT));
    assertEquals(labjackAdcFrame.getADC(2), Quantity.of(3.0f, SI.VOLT));
    assertEquals(labjackAdcFrame.getADC(3), Quantity.of(4.0f, SI.VOLT));
    assertEquals(labjackAdcFrame.getADC(4), Quantity.of(-5.23f, SI.VOLT));
    Tensor tensor = labjackAdcFrame.allADC();
    Chop._05.requireClose(tensor, Tensors.fromString("{1.2[V], 2.9[V], 3[V], 4[V], -5.23[V]}"));
  }
}
