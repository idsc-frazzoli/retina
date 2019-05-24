// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class FlippedPlanarVmu931ImuTest extends TestCase {
  public void testAccXY() {
    Tensor vmu931AccXY = FlippedPlanarVmu931Imu.INSTANCE.accXY(Tensors.vector(1, 2));
    assertEquals(vmu931AccXY, Tensors.vector(-2, -1));
  }

  public void testGyroZ() {
    Scalar gyroZ = FlippedPlanarVmu931Imu.INSTANCE.gyroZ(RealScalar.of(2));
    assertEquals(gyroZ, RealScalar.of(-2));
  }

  /** post 20190208: the sensor is flipped upside down and rotated by 90[deg]
   * in the XY plane, this corresponds to a mirror operation */
  public void testVmu931AccXY() {
    PlanarVmu931Imu planarVmu931Imu = FlippedPlanarVmu931Imu.INSTANCE;
    Tensor matrix = Tensor.of(IdentityMatrix.of(2).stream().map(planarVmu931Imu::accXY));
    assertEquals(Det.of(matrix), RealScalar.ONE.negate());
    assertEquals(planarVmu931Imu.accXY(Tensors.vector(1, 2)), Tensors.vector(-2, -1));
  }

  public void testVmu931GyroZ() {
    PlanarVmu931Imu planarVmu931Imu = FlippedPlanarVmu931Imu.INSTANCE;
    assertEquals(planarVmu931Imu.gyroZ(RealScalar.of(2)), RealScalar.of(-2));
  }
}
