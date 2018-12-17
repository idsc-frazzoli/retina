package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BrakingFunctionTest extends TestCase {
  public void testInversion() {
    Scalar wa = Quantity.of(0.01, SI.ACCELERATION);
    Scalar brakepos = BrakingFunction.getNeededBrakeActuation(wa);
    Scalar wa2 = BrakingFunction.getBrakingAcceleration(brakepos);
    System.out.println("braking position: " + brakepos);
    System.out.println("wa: " + wa);
    System.out.println("wa2: " + wa2);
    System.out.println("[0-1]: " + BrakingFunction.getRelativePosition(brakepos));
    assertTrue(Chop._10.close(wa, wa2));
  }

  public void testBigValues() {
    // only important that it does not crash here
    Scalar brakepos1 = BrakingFunction.getNeededBrakeActuation(//
        Quantity.of(1, SI.ACCELERATION));
    System.out.println("braking position: " + brakepos1);
    Scalar brakepos2 = BrakingFunction.getNeededBrakeActuation(//
        Quantity.of(2, SI.ACCELERATION));
    System.out.println("braking position: " + brakepos2);
    Scalar brakepos3 = BrakingFunction.getNeededBrakeActuation(//
        Quantity.of(3, SI.ACCELERATION));
    System.out.println("braking position: " + brakepos3);
    Scalar brakepos4 = BrakingFunction.getNeededBrakeActuation(//
        Quantity.of(30, SI.ACCELERATION));
    System.out.println("braking position: " + brakepos4);
    Scalar brakepos5 = BrakingFunction.getNeededBrakeActuation(//
        Quantity.of(50, SI.ACCELERATION));
    System.out.println("braking position: " + brakepos5);
  }
}
