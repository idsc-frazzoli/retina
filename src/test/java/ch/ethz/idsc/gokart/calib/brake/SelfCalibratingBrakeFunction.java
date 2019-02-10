package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SelfCalibratingBrakeFunction extends TestCase {
  public void testCalibration() {
    Scalar brakeFade = RealScalar.of(0.8);
    Scalar brakeDeceleration = Quantity.of(3.5, SI.ACCELERATION);
    Scalar realSpeed = Quantity.of(5, SI.VELOCITY);
    SelfCalibratingBrakingFunction correctingBrakingFunction = SelfCalibratingBrakingFunction.getInstance();
    BrakingFunction brakingFunction = BrakingFunction.getInstance();
    for (int i = 0; i < 1000; i++) {
      // simulate step
      Scalar brakePos = correctingBrakingFunction.getNeededBrakeActuation(brakeDeceleration);
      Scalar realBrakeDeceleration = brakingFunction.getAcceleration(brakePos).multiply(brakeFade);
      correctingBrakingFunction.correctBraking(brakeDeceleration, realBrakeDeceleration, realSpeed, realSpeed);
      System.out.println("wanted braking: " + brakeDeceleration + "/realBraking");
    }
  }
}
