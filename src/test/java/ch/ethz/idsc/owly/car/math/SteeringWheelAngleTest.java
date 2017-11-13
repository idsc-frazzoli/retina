// code by jph
package ch.ethz.idsc.owly.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class SteeringWheelAngleTest extends TestCase {
  public void testSimple() {
    Scalar a1 = SteeringWheelAngle.of(RealScalar.of(+.5), RealScalar.of(.3));
    Scalar a2 = SteeringWheelAngle.of(RealScalar.of(-.5), RealScalar.of(.3));
    assertTrue(a1.toString().startsWith("0.26175"));
    assertTrue(a2.toString().startsWith("0.35079"));
  }

  public void test90() {
    Scalar a1 = SteeringWheelAngle.of(RealScalar.of(+.5), RealScalar.of(Math.PI / 2));
    Scalar a2 = SteeringWheelAngle.of(RealScalar.of(-.5), RealScalar.of(Math.PI / 2));
    assertTrue(a1.toString().startsWith("1.1071487177940"));
    assertTrue(a2.toString().startsWith("-1.1071487177940"));
  }
}
