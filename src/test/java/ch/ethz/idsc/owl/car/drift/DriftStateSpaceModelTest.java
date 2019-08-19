// code by edo
package ch.ethz.idsc.owl.car.drift;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DriftStateSpaceModelTest extends TestCase {
  public void testSimple() {
    DriftParameters driftParameters = new DriftParameters();
    DriftStateSpaceModel driftStateSpaceModel = new DriftStateSpaceModel(driftParameters);
    {
      Tensor dx = driftStateSpaceModel.f(Tensors.vector(0, 0, 1), Tensors.vector(0, 0));
      assertEquals(dx, Tensors.vector(0, 0, 0));
    }
    {
      Tensor dx = driftStateSpaceModel.f(Tensors.vector(3, 2, 1), Tensors.vector(0, 0));
      Tensor apx = Tensors.vector(-3.382516701947507, -4.347657170890493, 6.0);
      assertTrue(Chop._10.close(dx, apx));
    }
    {
      Tensor dx = driftStateSpaceModel.f(Tensors.vector(1, -1, 2), Tensors.vector(0, 0));
      Tensor apx = Tensors.vector(-1.366070400816029, -0.20515690244009585, -2.0);
      assertTrue(Chop._10.close(dx, apx));
    }
    {
      Tensor dx = driftStateSpaceModel.f(Tensors.vector(-2, 1, -1), Tensors.vector(1, 1000));
      Tensor apx = Tensors.vector(3.4237125884714423, -0.3323667425515997, 5.265978254225968);
      Chop._10.requireClose(dx, apx);
    }
  }

  public void testArcTan() {
    DriftParameters driftParameters = new DriftParameters();
    DriftStateSpaceModel driftStateSpaceModel = new DriftStateSpaceModel(driftParameters);
    {
      Tensor dx = driftStateSpaceModel.f(Tensors.vector(-2, 1, -1), Tensors.vector(1, 1000));
      Tensor apx = Tensors.vector(3.4237125884714423, -0.3323667425515997, 5.265978254225968);
      Chop._10.requireClose(dx, apx);
    }
    {
      Tensor dx = driftStateSpaceModel.f(Tensors.vector(-2, 1, 1), Tensors.vector(1, 1000));
      Tensor apx = Tensors.vector(3.36978486305964, 0.22265872608088408, -3.7758621347332166);
      Chop._10.requireClose(dx, apx);
    }
  }
}
