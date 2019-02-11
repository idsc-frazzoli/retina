package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SelfCalibratingBrakeFunctionTest extends TestCase {
  public void testCalibration() {
    Scalar brakeFade = RealScalar.of(0.8);
    Scalar brakeDeceleration = Quantity.of(2.7, SI.ACCELERATION);
    Scalar realSpeed = Quantity.of(5, SI.VELOCITY);
    SelfCalibratingBrakingFunction correctingBrakingFunction = BrakingFunctions.CALIBRATING;
    // SelfCalibratingBrakingFunctionConfig.GLOBAL.geodesicFilterAlpha = RealScalar.of(0.1);
    StaticBrakingFunction brakingFunction = BrakingFunctions.STATIC;
    for (int i = 0; i < 1000; i++) {
      // simulate step
      Scalar brakePos = correctingBrakingFunction.getNeededBrakeActuation(brakeDeceleration);
      // System.out.println("brakepos: "+brakePos);
      Scalar realBrakeDeceleration = brakingFunction.getDeceleration(brakePos).multiply(brakeFade);
      correctingBrakingFunction.correctBraking(brakeDeceleration, realBrakeDeceleration, realSpeed, realSpeed);
      // System.out.println("wanted braking: " + brakeDeceleration + "/realBraking: "+realBrakeDeceleration);
      // System.out.println("brake fade: " + correctingBrakingFunction.getBrakeFadeFactor());
      if (i == 999) {
        assertTrue(Chop._03.close(brakeDeceleration, realBrakeDeceleration));
      }
    }
  }
}
