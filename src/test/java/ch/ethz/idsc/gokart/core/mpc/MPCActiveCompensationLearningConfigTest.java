// code by jph 
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class MPCActiveCompensationLearningConfigTest extends TestCase {
  public void testNegativeAccelerationCorrectionRate() {
    Scalar scalar = MPCActiveCompensationLearningConfig.GLOBAL.negativeAccelerationCorrectionRate;
    Sign.requirePositive(QuantityMagnitude.SI().in("m^-2*s^3").apply(scalar));
  }

  public void testSteeringCorrectionRate() {
    Scalar scalar = MPCActiveCompensationLearningConfig.GLOBAL.steeringCorrectionRate;
    Sign.requirePositiveOrZero(QuantityMagnitude.SI().in("m").apply(scalar));
  }
}
