// code by jph
package ch.ethz.idsc.owly.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Cos;
import junit.framework.TestCase;

public class DifferentialSpeedTest extends TestCase {
  public void testSimple() {
    DifferentialSpeed ds = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(.5));
    Scalar v = RealScalar.of(4);
    Scalar beta = RealScalar.of(+.3);
    // confirmed with mathematica
    assertTrue(Chop._10.close(ds.get(v.divide(Cos.FUNCTION.apply(beta)), beta), RealScalar.of(3.4844395839839613)));
    assertEquals(ds.get(v, RealScalar.ZERO), v);
    assertTrue(Chop._10.close(ds.get(v.divide(Cos.FUNCTION.apply(beta)), beta.negate()), RealScalar.of(4.515560416016039)));
  }

  public void testStraight() {
    DifferentialSpeed dsL = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(+.5));
    DifferentialSpeed dsR = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(-.5));
    Scalar v = RealScalar.of(-4);
    Scalar beta = RealScalar.ZERO;
    Scalar rL = dsL.get(v, beta);
    Scalar rR = dsR.get(v, beta);
    assertEquals(rL, v);
    assertEquals(rR, v);
  }

  public void testOrthogonal() {
    DifferentialSpeed dsL = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(+.5));
    DifferentialSpeed dsR = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(-.5));
    Scalar v = RealScalar.of(4);
    Scalar beta = RealScalar.of(Math.PI / 2);
    Scalar rL = dsL.get(v, beta);
    Scalar rR = dsR.get(v, beta);
    assertTrue(Chop._12.close(rL, rR.negate()));
  }

  public void testInverted() {
    DifferentialSpeed ds = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(-.5));
    Scalar v = RealScalar.of(4);
    Scalar beta = RealScalar.of(+.3);
    // confirmed with mathematica
    assertTrue(Chop._10.close(ds.get(v.divide(Cos.FUNCTION.apply(beta)), beta), RealScalar.of(4.515560416016039)));
    assertEquals(ds.get(v, RealScalar.ZERO), v);
    assertTrue(Chop._10.close(ds.get(v.divide(Cos.FUNCTION.apply(beta)), beta.negate()), RealScalar.of(3.4844395839839613)));
  }

  public void testFail() {
    try {
      new DifferentialSpeed(RealScalar.of(0.0), RealScalar.of(.5));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
