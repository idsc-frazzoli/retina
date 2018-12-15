// code by jph
package ch.ethz.idsc.retina.dev.u3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class LabjackAdcFrameTest extends TestCase {
  public void testBPFalse() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 0f, 0f });
    assertFalse(labjackAdcFrame.isBoostPressed());
  }

  public void testBPTrue() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 0f, 0f, 2.4f });
    assertTrue(labjackAdcFrame.isBoostPressed());
  }

  public void testThrottle() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 2.5f, 0f, 0f });
    Scalar scalar = labjackAdcFrame.getAheadSigned();
    assertTrue(Scalars.lessThan(scalar.subtract(RationalScalar.HALF).abs(), RealScalar.of(.05)));
  }

  public void testThrottleMax() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 5.3f, 0f, 0f });
    Scalar scalar = labjackAdcFrame.getAheadSigned();
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testThrottleNegative() {
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(new float[] { 0f, 0f, 2.5f, 0f, 2f });
    Scalar scalar = labjackAdcFrame.getAheadSigned();
    assertTrue(Scalars.lessThan(scalar.subtract(RationalScalar.HALF.negate()).abs(), RealScalar.of(.05)));
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
    assertFalse(labjackAdcFrame.isBoostPressed());
  }
}
