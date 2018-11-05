package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class BrakingFunctionTest extends TestCase {
  public void testInversion() {
    Scalar wa = Quantity.of(0.01, SI.ACCELERATION);
   Scalar brakepos = BrakingFunction.getNeededBrakeActuation(wa);
   Scalar wa2 = BrakingFunction.getBrakingAcceleration(brakepos);
    System.out.println("braking position: "+brakepos);
    System.out.println("wa: "+wa);
    System.out.println("wa2: "+wa2);
    System.out.println("[0-1]: "+BrakingFunction.getRelativePosition(brakepos));
  }
}
