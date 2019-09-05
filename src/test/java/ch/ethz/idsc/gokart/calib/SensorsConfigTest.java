// code by jph
package ch.ethz.idsc.gokart.calib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class SensorsConfigTest extends TestCase {
  public void testSimple() {
    VectorQ.ofLength(SensorsConfig.GLOBAL.vlp16_pose, 3);
    PoseHelper.toUnitless(SensorsConfig.GLOBAL.vlp16_pose);
    SensorsConfig.GLOBAL.vlp16Gokart();
    Clips.interval(Quantity.of(0.05, SI.METER), Quantity.of(0.15, SI.METER)).requireInside(SensorsConfig.GLOBAL.vlp16_pose.Get(0));
    assertTrue(Scalars.isZero(SensorsConfig.GLOBAL.vlp16_pose.Get(1)));
    assertTrue(Scalars.isZero(SensorsConfig.GLOBAL.vlp16_pose.Get(2)));
  }

  public void testVlp16FrontFacing() {
    assertEquals(SensorsConfig.GLOBAL.vlp16_twist, RealScalar.of(-1.61));
    assertTrue(Scalars.isZero(SensorsConfig.GLOBAL.vlp16_pose.Get(2)));
    assertTrue(Scalars.isZero(new SensorsConfig().vlp16_pose.Get(2)));
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
    Clip clip = Clips.interval( //
        Quantity.of(-0.56, SI.PER_SECOND), //
        Quantity.of(-0.50, SI.PER_SECOND));
    clip.requireInside(gyroZ);
  }

  public void testvlp16_relativeZero() {
    Clips.interval(0.7, 0.8).requireInside(SensorsConfig.GLOBAL.vlp16_relativeZero);
  }

  public void testUnits() {
    TensorUnaryOperator toPolar = SensorsConfig.GLOBAL.vlp16ToPolarCoordinates();
    TensorUnaryOperator fromPolar = SensorsConfig.GLOBAL.vlp16FromPolarCoordinates();
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor xyz = RandomVariate.of(distribution, 3).multiply(Quantity.of(1, SI.METER));
      Tensor tensor = toPolar.apply(xyz);
      Tensor inv = fromPolar.apply(tensor);
      Chop._12.requireClose(xyz, inv);
    }
  }

  public void testVlp16Geometry() {
    Sign.requirePositive(SensorsConfig.GLOBAL.vlp16_poseX_inMeter());
    Tensor vlp16_sideMatrix = SensorsConfig.GLOBAL.vlp16_sideMatrix();
    Chop._12.requireClose(Det.of(vlp16_sideMatrix), RealScalar.ONE);
  }
}
