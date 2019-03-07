// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DavisImuFrameTest extends TestCase {
  public void testSimple() {
    assertEquals(DavisImuFrame.LENGTH, 18);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4 + 2 * 7]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putInt(0x12345678);
    byteBuffer.putShort((short) 102);
    byteBuffer.putShort((short) 120);
    byteBuffer.putShort((short) 220);
    byteBuffer.putShort((short) (340 * 12));
    byteBuffer.putShort((short) 120);
    byteBuffer.putShort((short) 2200);
    byteBuffer.putShort((short) -4233);
    byteBuffer.flip();
    DavisImuFrame dif = new DavisImuFrame(byteBuffer);
    Tensor acc = Tensors.fromString("{0.2442919921875[m*s^-2], -0.28740234375[m*s^-2], 0.5269042968750001[m*s^-2]}");
    assertEquals(dif.accelImageFrame(), acc);
    assertEquals(dif.temperature(), Quantity.of(47, "degC"));
    Tensor gyroImage = Tensors.fromString("{-0.063950995492922[s^-1], 1.1724349173702366[s^-1], 2.2558713660128236[s^-1]}");
    assertEquals(dif.gyroImageFrame(), gyroImage);
    assertEquals(dif.getTime(), Quantity.of(0x12345678, "us"));
    assertEquals(dif.getTimeRelativeTo(0x12345670), Quantity.of(8, "us"));
  }
}
