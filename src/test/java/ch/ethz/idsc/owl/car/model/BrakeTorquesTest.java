// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.CHatchbackModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BrakeTorquesTest extends TestCase {
  public void testSimple() {
    VehicleModel carModel = CHatchbackModel.standard();
    CarState carState = CarStatic.x0_demo1(carModel);
    CarControl cc = carModel.createControl(Tensors.vector(0, 1, 0, 0));
    Scalar mu = RealScalar.of(0.8); // friction coefficient on dry road
    TireForces tireForces = new TireForces(carModel, carState, cc, mu);
    BrakeTorques brakes = new BrakeTorques(carModel, carState, cc, tireForces);
    assertEquals(brakes.torque(0), brakes.torque(1));
    assertTrue(Scalars.lessThan(brakes.torque(0), RealScalar.ZERO));
    assertEquals(brakes.torque(2), brakes.torque(3));
    assertTrue(Scalars.lessThan(brakes.torque(2), RealScalar.ZERO));
  }
}
