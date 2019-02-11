// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public final class StaticBrakeFunction extends AbstractBrakeFunction {
  public static final StaticBrakeFunction INSTANCE = new StaticBrakeFunction();

  // ---
  private StaticBrakeFunction() {
  }

  @Override // @Deprecated // TODO: JPH, why is this Deprecated?
  public Scalar getDeceleration(Scalar brakingPosition) {
    return getDeceleration(brakingPosition, RealScalar.ONE);
  }

  @Override
  public Scalar getNeededBrakeActuation(Scalar wantedDeceleration) {
    return getNeededBrakeActuation(wantedDeceleration, RealScalar.ONE);
  }
}
