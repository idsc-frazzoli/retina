// code by jph
package ch.ethz.idsc.owl.car.shop;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoSinusIonModelTest extends TestCase {
  public void testSimple() {
    VehicleModel vehicleModel = RimoSinusIonModel.standard();
    Tensor fp = vehicleModel.footprint();
    assertEquals(Dimensions.of(fp).get(1), (Integer) 3);
  }

  public void testBounds() {
    VehicleModel vehicleModel = RimoSinusIonModel.standard();
    Tensor tensor = vehicleModel.footprint();
    MinMax minMax = MinMax.of(tensor);
    Chop._10.requireClose(minMax.min(), Tensors.fromString("{-0.295, -0.725, -0.25}"));
    Chop._10.requireClose(minMax.max(), Tensors.fromString("{1.765, 0.725, -0.25}"));
  }
}
