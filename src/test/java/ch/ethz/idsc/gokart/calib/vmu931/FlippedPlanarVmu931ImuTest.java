// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
}
