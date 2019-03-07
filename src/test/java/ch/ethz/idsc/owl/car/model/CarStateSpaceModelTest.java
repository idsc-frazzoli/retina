// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.CHatchbackModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CarStateSpaceModelTest extends TestCase {
  @SuppressWarnings("unused")
  public void testSimple() {
    VehicleModel carModel = CHatchbackModel.standard();
    CarState carState = CarStatic.x0_demo3();
    // CarControl carControl = carModel.createControl();
    Tensor u = Tensors.vector(0.4, .03, 0, .4);
    StateSpaceModel ssm = new CarStateSpaceModel(carModel, HomogenousTrack.DRY_ROAD);
    // LONGTERM units don't match anymore!
    // Tensor res = ssm.f(carState.asVector(), u);
    // Tensor gnd = Tensors.fromString(
    // "{1.4243749104432206, 2.1618367009520716, -1.8297547914814403, 0.4, 5.0631654668445165, -2.4241717228308928, -1231.189048069456, 764.091380010576,
    // -981.052814181167, 268.57867140004987}");
    // assertTrue(Chop._07.allZero(res.subtract(gnd)));
  }
}
