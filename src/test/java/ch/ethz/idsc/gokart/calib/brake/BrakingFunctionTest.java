// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class BrakingFunctionTest extends TestCase {
  public void testBrakingAccel() {
    Scalar scalar = BrakingFunction.getAcceleration(Quantity.of(0.03, "m"));
    // System.out.println("HERE=" + scalar);
    Clip.function(Quantity.of(1.85, "m*s^-2"), Quantity.of(1.86, "m*s^-2")).requireInside(scalar);
  }

  public void testBrakingAccelZero() {
    assertEquals(Quantity.of(0, "m*s^-2"), BrakingFunction.getAcceleration(Quantity.of(0.00, "m")));
    assertEquals(Quantity.of(0, "m*s^-2"), BrakingFunction.getAcceleration(Quantity.of(0.025, "m")));
  }

  public void testAccelPushLimit() {
    BrakingFunction.getAcceleration(Quantity.of(0.05, "m"));
    // System.out.println("TEST=" + scalar);
    // Clip.function(Quantity.of(1.85, "m*s^-2"), Quantity.of(1.86, "m*s^-2")).requireInside(scalar);
  }

  public void testInversion() {
    Scalar wa1 = Quantity.of(2, SI.ACCELERATION);
    Scalar brakepos = BrakingFunction.getNeededBrakeActuation(wa1);
    Scalar wa2 = BrakingFunction.getAcceleration(brakepos);
    System.out.println("braking position: " + brakepos);
    System.out.println("wa1: " + wa1);
    System.out.println("wa2: " + wa2);
    System.out.println("[0-1]: " + BrakingFunction.getRelativePosition(brakepos));
    Chop._10.requireClose(wa1, wa2);
  }

  public void testZeroNeg() {
    assertEquals(BrakingFunction.getRelativeBrakeActuation(Quantity.of(+0, SI.ACCELERATION)), RealScalar.ZERO);
    assertEquals(BrakingFunction.getRelativeBrakeActuation(Quantity.of(-1, SI.ACCELERATION)), RealScalar.ZERO);
    assertEquals(BrakingFunction.getRelativeBrakeActuation(Quantity.of(-2, SI.ACCELERATION)), RealScalar.ZERO);
  }

  public void testBigValues() {
    // only important that it does not crash here
    Scalar brakepos1 = BrakingFunction.getNeededBrakeActuation(Quantity.of(1, SI.ACCELERATION));
    // System.out.println(brakepos1);
    Clip.function(Quantity.of(0.025, "m"), Quantity.of(0.030, "m")).requireInside(brakepos1);
    Scalar brakepos2 = BrakingFunction.getNeededBrakeActuation(Quantity.of(2, SI.ACCELERATION));
    Clip.function(Quantity.of(0.030, "m"), Quantity.of(0.035, "m")).requireInside(brakepos2);
    Scalar brakepos3 = BrakingFunction.getNeededBrakeActuation(Quantity.of(3, SI.ACCELERATION));
    Clip.function(Quantity.of(0.030, "m"), Quantity.of(0.035, "m")).requireInside(brakepos3);
    Scalar brakepos4 = BrakingFunction.getNeededBrakeActuation(Quantity.of(30, SI.ACCELERATION));
    Clip.function(Quantity.of(0.040, "m"), Quantity.of(0.045, "m")).requireInside(brakepos4);
    Scalar brakepos5 = BrakingFunction.getNeededBrakeActuation(Quantity.of(50, SI.ACCELERATION));
    Clip.function(Quantity.of(0.040, "m"), Quantity.of(0.045, "m")).requireInside(brakepos5);
    assertEquals(brakepos4, brakepos5);
  }

  public void testGetRelative() {
    Scalar scalar = BrakingFunction.getRelativePosition(Quantity.of(0.02, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(0.33333333333333326));
    scalar = BrakingFunction.getRelativePosition(Quantity.of(0.05, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(1));
    scalar = BrakingFunction.getRelativePosition(Quantity.of(0.1, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(1));
    scalar = BrakingFunction.getRelativePosition(Quantity.of(0.0, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(0));
    scalar = BrakingFunction.getRelativePosition(Quantity.of(-0.3, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(0));
  }
}
