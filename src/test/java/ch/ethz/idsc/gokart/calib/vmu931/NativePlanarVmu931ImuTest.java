// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class NativePlanarVmu931ImuTest extends TestCase {
  public void testSimple() {
    assertEquals(NativePlanarVmu931Imu.INSTANCE.accXY(Tensors.vector(1, 2)), Tensors.vector(1, 2));
    assertEquals(NativePlanarVmu931Imu.INSTANCE.gyroZ(RealScalar.of(2)), RealScalar.of(2));
  }
}
