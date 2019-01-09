// code by mh and jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class BrakingFunctionTest extends TestCase {
  public void testBrakingAccel() {
    Scalar scalar = BrakingFunction.getBrakingAcceleration(Quantity.of(0.03, "m"));
    Clip.function(Quantity.of(1.85, "m*s^-2"), Quantity.of(1.86, "m*s^-2")).requireInside(scalar);
  }

  public void testBrakingAccelZero() {
    Scalar scalar = BrakingFunction.getBrakingAcceleration(Quantity.of(0.00, "m"));
    Clip.function(Quantity.of(0, "m*s^-2"), Quantity.of(0, "m*s^-2")).requireInside(scalar);
  }

  public void testInversion() {
    Scalar wa = Quantity.of(2, SI.ACCELERATION);
    Scalar brakepos = BrakingFunction.getNeededBrakeActuation(wa);
    Scalar wa2 = BrakingFunction.getBrakingAcceleration(brakepos);
    System.out.println("braking position: " + brakepos);
    System.out.println("wa: " + wa);
    System.out.println("wa2: " + wa2);
    System.out.println("[0-1]: " + BrakingFunction.getRelativePosition(brakepos));
    Chop._10.requireClose(wa, wa2);
  }

  public void testBigValues() {
    // only important that it does not crash here
    Scalar brakepos1 = BrakingFunction.getNeededBrakeActuation(Quantity.of(1, SI.ACCELERATION));
    Clip.function(Quantity.of(0.025, "m"), Quantity.of(0.030, "m")).requireInside(brakepos1);
    // System.out.println("braking position: " + brakepos1);
    Scalar brakepos2 = BrakingFunction.getNeededBrakeActuation(Quantity.of(2, SI.ACCELERATION));
    Clip.function(Quantity.of(0.030, "m"), Quantity.of(0.035, "m")).requireInside(brakepos2);
    // System.out.println("braking position: " + brakepos2);
    Scalar brakepos3 = BrakingFunction.getNeededBrakeActuation(Quantity.of(3, SI.ACCELERATION));
    Clip.function(Quantity.of(0.030, "m"), Quantity.of(0.035, "m")).requireInside(brakepos3);
    // System.out.println("braking position: " + brakepos3);
    Scalar brakepos4 = BrakingFunction.getNeededBrakeActuation(Quantity.of(30, SI.ACCELERATION));
    Clip.function(Quantity.of(0.040, "m"), Quantity.of(0.045, "m")).requireInside(brakepos4);
    // System.out.println("braking position: " + brakepos4);
    Scalar brakepos5 = BrakingFunction.getNeededBrakeActuation(Quantity.of(50, SI.ACCELERATION));
    Clip.function(Quantity.of(0.040, "m"), Quantity.of(0.045, "m")).requireInside(brakepos5);
    assertEquals(brakepos4, brakepos5);
    // System.out.println("braking position: " + brakepos5);
    // System.out.println("---");
  }
}
