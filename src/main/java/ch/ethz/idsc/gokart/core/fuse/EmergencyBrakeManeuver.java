// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.red.Times;

/** the current implementation works with
 * maxDeceleration <= 0, velocity >= 0
 * maxDeceleration >= 0, velocity <= 0 */
public class EmergencyBrakeManeuver {
  private static final Scalar NEGATIVE_HALF = RealScalar.of(-0.5);
  // ---
  /** duration to full stop with unit "s" */
  public final Scalar duration;
  /** distance to full stop with unit "m" */
  public final Scalar distance;

  /** @param responseTime with unit "s"
   * @param maxDeceleration with unit "m*s^-2"
   * @param velocity with unit "m*s^-1" and different sign than maxDeceleration */
  public EmergencyBrakeManeuver(Scalar responseTime, Scalar maxDeceleration, Scalar velocity) {
    Scalar d0 = velocity.multiply(responseTime); // [m]
    Scalar bt = velocity.divide(maxDeceleration).negate();
    Scalar d1 = Times.of(NEGATIVE_HALF, maxDeceleration, bt, bt);
    duration = responseTime.add(bt);
    distance = d0.add(d1);
  }

  public long getDuration_ms() {
    return Magnitude.MILLI_SECOND.toLong(duration);
  }

  /** @param contact with unit "m"
   * @return true if the distance to contact is less than the distance estimated for braking */
  public boolean isRequired(Scalar contact) {
    return Scalars.lessThan(contact, distance); // TODO only works with distance>0
  }

  public String toInfoString() {
    return "brake: " + duration + " " + distance;
  }
}
