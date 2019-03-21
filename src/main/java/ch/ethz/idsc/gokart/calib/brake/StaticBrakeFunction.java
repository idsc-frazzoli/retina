// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class StaticBrakeFunction extends AbstractBrakeFunction {
  public static final AbstractBrakeFunction INSTANCE = new StaticBrakeFunction();
  // ---
  private static final Scalar ONE = RealScalar.of(1.0);

  private StaticBrakeFunction() {
  }

  @Override // from AbstractBrakeFunction
  Scalar getDeceleration(Scalar brakingPosition) {
    return getDeceleration(brakingPosition, ONE);
  }

  @Override // from AbstractBrakeFunction
  Scalar getNeededBrakeActuation(Scalar wantedDeceleration) {
    return getNeededBrakeActuation(wantedDeceleration, ONE);
  }
}
