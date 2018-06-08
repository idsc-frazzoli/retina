// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class EmergencyBrakeManeuver {
  private static final ScalarUnaryOperator IN_MILLIS = QuantityMagnitude.SI().in("ms");
  // ---
  /** duration to full stop with unit "s" */
  public final Scalar duration;
  /** distance to full stop with unit "m" */
  public final Scalar distance;

  /** @param responseTime with unit "s"
   * @param maxDeceleration with unit "m*s^-2"
   * @param velocity with unit "m*s^-1" */
  public EmergencyBrakeManeuver(Scalar responseTime, Scalar maxDeceleration, Scalar velocity) {
    Scalar d0 = velocity.multiply(responseTime);
    Scalar bt = velocity.divide(maxDeceleration).negate();
    Scalar d1 = Times.of(RationalScalar.HALF.negate(), maxDeceleration, bt, bt);
    duration = responseTime.add(bt);
    distance = d0.add(d1);
  }

  public long getDuration_ms() {
    return IN_MILLIS.apply(duration).number().longValue();
  }

  /** @param contact with unit "m"
   * @return true if the distance to contact is less than the distance estimated for braking */
  public boolean isRequired(Scalar contact) {
    return Scalars.lessThan(contact, distance);
  }

  public String toInfoString() {
    return "brake: " + duration + " " + distance;
  }
}
