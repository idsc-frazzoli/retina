// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MinMaxTest extends TestCase {
  public void testSimple() {
    VehicleModel STANDARD = RimoSinusIonModel.standard();
    Tensor tensor = STANDARD.footprint();
    MinMax minMax = MinMax.of(tensor);
    System.out.println(minMax.max());
    assertTrue(Chop._10.close(minMax.min(), Tensors.fromString("{-0.295, -0.725, -0.25}")));
    assertTrue(Chop._10.close(minMax.max(), Tensors.fromString("{1.765, 0.725, -0.25}")));
  }
}
