// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import junit.framework.TestCase;

public class PlanarVmu931TypeTest extends TestCase {
  public void testSimple() {
    for (PlanarVmu931Type planarVmu931Type : PlanarVmu931Type.values()) {
      Tensor accXY = planarVmu931Type.planarVmu931Imu().accXY(Tensors.vector(0, 0));
      assertEquals(accXY, Array.zeros(2));
    }
  }
}
