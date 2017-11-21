// code by jph
package ch.ethz.idsc.owly.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Cos;
import junit.framework.TestCase;

public class DifferentialSpeedTest extends TestCase {
  public void testSimple() {
    DifferentialSpeed ds = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(.5));
    Scalar speed = RealScalar.of(+4.0);
    Scalar angle = RealScalar.of(+0.3);
    // confirmed with mathematica
    assertTrue(Chop._10.close(ds.get(speed.divide(Cos.FUNCTION.apply(angle)), angle), RealScalar.of(3.4844395839839613)));
    assertEquals(ds.get(speed, RealScalar.ZERO), speed);
    assertTrue(Chop._10.close(ds.get(speed.divide(Cos.FUNCTION.apply(angle)), angle.negate()), RealScalar.of(4.515560416016039)));
  }

  public void testQuantityForward() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = new DifferentialSpeed(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = new DifferentialSpeed(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(+4.0, "m*s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
    }
  }

  public void testQuantityPair() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = new DifferentialSpeed(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = new DifferentialSpeed(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(+4.0, "m*s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
      Tensor pair = tireRearL.pair(speed, angle);
      assertEquals(pair, Tensors.of(tireL, tireR));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
      Tensor pair = tireRearL.pair(speed, angle);
      assertEquals(pair, Tensors.of(tireL, tireR));
    }
  }

  public void testQuantityReverse() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = new DifferentialSpeed(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = new DifferentialSpeed(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(-4.0, "m*s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
    }
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
