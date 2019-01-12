// code by jph
package ch.ethz.idsc.retina.u3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class LabjackAdcFrameTest extends TestCase {
  public void testBPFalse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 0f, 0f });
    assertEquals(labjackAdcFrame.getADC_V(0), 0f);
  }

  public void testBPTrue() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 0f, 2.4f });
    assertEquals(labjackAdcFrame.getADC_V(4), 2.4f);
  }

  public void testThrottle() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 2.5f, 0f, 0f });
    assertEquals(labjackAdcFrame.getADC_V(2), 2.5f);
  }

  public void testThrottleMax() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 5.3f, 0f, 0f });
    assertEquals(labjackAdcFrame.getADC_V(2), 5.3f);
  }

  public void testThrottleNegative() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 2.5f, 0f, 2f });
    assertEquals(labjackAdcFrame.getADC_V(2), 2.5f);
    assertEquals(labjackAdcFrame.getADC_V(4), 2f);
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
    assertEquals(labjackAdcFrame.getADC_V(0), 1.2f);
    assertEquals(labjackAdcFrame.getADC_V(1), 2.9f);
    assertEquals(labjackAdcFrame.getADC_V(2), 3.0f);
    assertEquals(labjackAdcFrame.getADC_V(3), 4.0f);
    assertEquals(labjackAdcFrame.getADC_V(4), -5.23f);
  }
}
