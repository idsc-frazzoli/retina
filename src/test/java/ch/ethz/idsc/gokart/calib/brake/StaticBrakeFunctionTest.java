// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class StaticBrakeFunctionTest extends TestCase {
  public void testBrakingAccel() {
    Scalar scalar = StaticBrakeFunction.INSTANCE.getDeceleration(Quantity.of(0.03, "m"));
    // System.out.println("HERE=" + scalar);
    Clip.function(Quantity.of(1.25, "m*s^-2"), Quantity.of(1.30, "m*s^-2")).requireInside(scalar);
  }

  public void testBrakingAccelZero() {
    assertEquals(Quantity.of(0, "m*s^-2"), StaticBrakeFunction.INSTANCE.getDeceleration(Quantity.of(0.00, "m")));
    assertEquals(Quantity.of(0, "m*s^-2"), StaticBrakeFunction.INSTANCE.getDeceleration(Quantity.of(0.025, "m")));
  }

  public void testAccelPushLimit() {
    StaticBrakeFunction.INSTANCE.getDeceleration(Quantity.of(0.05, "m"));
    // System.out.println("TEST=" + scalar);
    // Clip.function(Quantity.of(1.85, "m*s^-2"), Quantity.of(1.86, "m*s^-2")).requireInside(scalar);
  }

  public void testInversion() {
    Scalar wa1 = Quantity.of(2, SI.ACCELERATION);
    Scalar brakepos = StaticBrakeFunction.INSTANCE.getNeededBrakeActuation(wa1);
    Scalar wa2 = StaticBrakeFunction.INSTANCE.getDeceleration(brakepos);
    System.out.println("braking position: " + brakepos);
    System.out.println("wa1: " + wa1);
    System.out.println("wa2: " + wa2);
    System.out.println("[0-1]: " + StaticBrakeFunction.getRelativePosition(brakepos));
    Chop._10.requireClose(wa1, wa2);
  }

  public void testInversionWithMultiplicator() {
    Scalar wa1 = Quantity.of(2, SI.ACCELERATION);
    Scalar fadeFactor = RealScalar.of(0.8);
    Scalar brakepos = AbstractBrakeFunction.getNeededBrakeActuation(wa1, fadeFactor);
    Scalar wa2 = StaticBrakeFunction.INSTANCE.getDeceleration(brakepos).multiply(fadeFactor);
    System.out.println("braking position (with fading): " + brakepos);
    System.out.println("wa1: " + wa1);
    System.out.println("wa2: " + wa2);
    System.out.println("[0-1]: " + StaticBrakeFunction.getRelativePosition(brakepos));
    Chop._10.requireClose(wa1, wa2);
  }

  public void testZeroNeg() {
    assertEquals(StaticBrakeFunction.INSTANCE.getRelativeBrakeActuation(Quantity.of(+0, SI.ACCELERATION)), RealScalar.ZERO);
    assertEquals(StaticBrakeFunction.INSTANCE.getRelativeBrakeActuation(Quantity.of(-1, SI.ACCELERATION)), RealScalar.ZERO);
    assertEquals(StaticBrakeFunction.INSTANCE.getRelativeBrakeActuation(Quantity.of(-2, SI.ACCELERATION)), RealScalar.ZERO);
  }

  public void testBigValues() {
    // only important that it does not crash here
    Scalar brakepos1 = StaticBrakeFunction.INSTANCE.getNeededBrakeActuation(Quantity.of(1, SI.ACCELERATION));
    // System.out.println(brakepos1);
    Clip.function(Quantity.of(0.025, "m"), Quantity.of(0.030, "m")).requireInside(brakepos1);
    Scalar brakepos2 = StaticBrakeFunction.INSTANCE.getNeededBrakeActuation(Quantity.of(2, SI.ACCELERATION));
    Clip.function(Quantity.of(0.030, "m"), Quantity.of(0.035, "m")).requireInside(brakepos2);
    Scalar brakepos3 = StaticBrakeFunction.INSTANCE.getNeededBrakeActuation(Quantity.of(3, SI.ACCELERATION));
    Clip.function(Quantity.of(0.035, "m"), Quantity.of(0.037, "m")).requireInside(brakepos3);
    Scalar brakepos4 = StaticBrakeFunction.INSTANCE.getNeededBrakeActuation(Quantity.of(30, SI.ACCELERATION));
    Clip.function(Quantity.of(0.140, "m"), Quantity.of(0.150, "m")).requireInside(brakepos4);
    Scalar brakepos5 = StaticBrakeFunction.INSTANCE.getNeededBrakeActuation(Quantity.of(50, SI.ACCELERATION));
    Clip.function(Quantity.of(0.220, "m"), Quantity.of(0.235, "m")).requireInside(brakepos5);
  }

  public void testGetRelative() {
    Scalar scalar = StaticBrakeFunction.getRelativePosition(Quantity.of(0.02, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(0.33333333333333326));
    scalar = StaticBrakeFunction.getRelativePosition(Quantity.of(0.05, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(1));
    scalar = StaticBrakeFunction.getRelativePosition(Quantity.of(0.1, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(1));
    scalar = StaticBrakeFunction.getRelativePosition(Quantity.of(0.0, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(0));
    scalar = StaticBrakeFunction.getRelativePosition(Quantity.of(-0.3, "m"));
    Chop._08.requireClose(scalar, RealScalar.of(0));
  }
}
