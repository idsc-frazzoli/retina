// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AckermannSteeringTest extends TestCase {
  public void testSimple() {
    AckermannSteering asL = new AckermannSteering(Quantity.of(1, "m"), Quantity.of(+0.4, "m"));
    AckermannSteering asR = new AckermannSteering(Quantity.of(1, "m"), Quantity.of(-0.4, "m"));
    Scalar delta = RealScalar.of(.2);
    Scalar aL = asL.angle(delta);
    assertTrue(Scalars.lessThan(delta, aL));
    Scalar aR = asR.angle(delta);
    assertTrue(Scalars.lessThan(aR, delta));
  }

  public void testId() {
    AckermannSteering asL = new AckermannSteering(Quantity.of(1, "m"), Quantity.of(+0, "m"));
    Scalar delta = RealScalar.of(.2);
    assertEquals(asL.angle(delta), delta);
  }

  public void testPair() {
    AckermannSteering asL = new AckermannSteering(Quantity.of(1, "m"), Quantity.of(+0.4, "m"));
    Scalar delta = RealScalar.of(.2);
    Tensor pair = asL.pair(delta);
    assertEquals(pair.Get(0), asL.angle(delta));
    AckermannSteering asR = new AckermannSteering(Quantity.of(1, "m"), Quantity.of(-0.4, "m"));
    assertEquals(pair.Get(1), asR.angle(delta));
  }
}
