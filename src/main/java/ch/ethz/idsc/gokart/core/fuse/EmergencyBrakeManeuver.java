// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Times;

public class EmergencyBrakeManeuver {
  /** duration to full stop with unit "s" */
  public final Scalar duration;
  /** distance to full stop with unit "m" */
  public final Scalar distance;

  public EmergencyBrakeManeuver(Scalar responseTime, Scalar maxDeceleration, Scalar velocity) {
    Scalar d0 = velocity.multiply(responseTime);
    Scalar bt = velocity.divide(maxDeceleration).negate();
    Scalar d1 = Times.of(RationalScalar.HALF.negate(), maxDeceleration, bt, bt);
    duration = responseTime.add(bt);
    distance = d0.add(d1);
  }
}
