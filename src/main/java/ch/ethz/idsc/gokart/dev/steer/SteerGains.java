// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

class SteerGains {
  private static final Unit UNIT_KI = Unit.of("SCE^-1*SCT*s^-1");
  private static final Unit UNIT_KP = Unit.of("SCE^-1*SCT");
  private static final Unit UNIT_KD = Unit.of("SCE^-1*SCT*s");
  // ---
  public final Scalar Ki;
  public final Scalar Kp;
  public final Scalar Kd;

  public SteerGains(Tensor vector) {
    Ki = Quantity.of(vector.Get(0), UNIT_KI);
    Kp = Quantity.of(vector.Get(1), UNIT_KP);
    Kd = Quantity.of(vector.Get(2), UNIT_KD);
  }
}
