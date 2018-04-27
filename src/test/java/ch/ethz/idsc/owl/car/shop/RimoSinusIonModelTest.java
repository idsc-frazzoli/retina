// code by jph
package ch.ethz.idsc.owl.car.shop;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class RimoSinusIonModelTest extends TestCase {
  public void testSimple() {
    VehicleModel vm = RimoSinusIonModel.standard();
    Tensor fp = vm.footprint();
    assertEquals(Dimensions.of(fp).get(1), (Integer) 3);
    // Tensor fpm = Tensor.of(fp.stream().map(t -> t.extract(0, 2).map(Round._2)));
    // Export.of(UserHome.file("gokart_footprint.csv"), fpm);
  }
}
