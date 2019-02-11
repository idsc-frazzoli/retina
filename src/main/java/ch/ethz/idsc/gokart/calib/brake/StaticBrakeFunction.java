// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public final class StaticBrakeFunction extends AbstractBrakeFunction {
  public static final StaticBrakeFunction INSTANCE = new StaticBrakeFunction();
  private static final Scalar ONE = RealScalar.of(1.0);

  // ---
  private StaticBrakeFunction() {
  }

  @Override
  Scalar getDeceleration(Scalar brakingPosition) {
    return getDeceleration(brakingPosition, ONE);
  }

  @Override
  Scalar getNeededBrakeActuation(Scalar wantedDeceleration) {
    return getNeededBrakeActuation(wantedDeceleration, ONE);
  }
}
