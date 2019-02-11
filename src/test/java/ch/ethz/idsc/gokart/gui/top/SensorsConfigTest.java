// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class SensorsConfigTest extends TestCase {
  public void testSimple() {
    VectorQ.ofLength(SensorsConfig.GLOBAL.urg04lx, 3);
    VectorQ.ofLength(SensorsConfig.GLOBAL.vlp16, 3);
  }

  public void testVlp16FrontFacing() {
    assertEquals(SensorsConfig.GLOBAL.vlp16_twist, RealScalar.of(-1.61));
    assertTrue(Scalars.isZero(SensorsConfig.GLOBAL.vlp16.Get(2)));
    assertTrue(Scalars.isZero(new SensorsConfig().vlp16.Get(2)));
  }

  public void testInclineSign() {
    Sign.requirePositive(SensorsConfig.GLOBAL.vlp16_incline);
    Sign.requirePositive(new SensorsConfig().vlp16_incline);
  }

  public void testImuSamplesPerLidarScan() {
    int samples = SensorsConfig.GLOBAL.imuSamplesPerLidarScan();
    assertEquals(samples, 50);
  }

  public void testImuGyroZ() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4 + 2 * 7]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putInt(0x12345678);
    byteBuffer.putShort((short) 102);
    byteBuffer.putShort((short) 120);
    byteBuffer.putShort((short) 220);
    byteBuffer.putShort((short) (340 * 12));
    byteBuffer.putShort((short) 120);
    byteBuffer.putShort((short) 1000);
    byteBuffer.putShort((short) -4233);
    byteBuffer.flip();
    DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
    Scalar gyroZ = SensorsConfig.GLOBAL.davisGyroZ(davisImuFrame);
    Clip clip = Clip.function( //
        Quantity.of(-0.56, SI.PER_SECOND), //
        Quantity.of(-0.50, SI.PER_SECOND));
    clip.requireInside(gyroZ);
  }

  /** post 20190208: the sensor is flipped upside down and rotated by 90[deg]
   * in the XY plane, this corresponds to a mirror operation */
  public void testVmu931AccXY() {
    Tensor matrix = Tensor.of(IdentityMatrix.of(2).stream().map(SensorsConfig.GLOBAL::vmu931AccXY));
    assertEquals(Det.of(matrix), RealScalar.ONE.negate());
  }
}
