// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Rot90PlanarVmu931ImuTest extends TestCase {
  public void testSimple() {
    PlanarVmu931Imu planarVmu931Imu = Rot90PlanarVmu931Imu.INSTANCE;
    Tensor accXY = planarVmu931Imu.accXY(Tensors.vector(1, 2));
    assertEquals(accXY, Tensors.vector(-2, 1));
  }
}
