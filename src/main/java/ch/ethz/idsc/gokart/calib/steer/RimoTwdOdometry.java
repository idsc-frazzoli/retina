// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.owl.car.core.TwdOdometry;
import ch.ethz.idsc.tensor.Scalar;

public class RimoTwdOdometry extends TwdOdometry {
  public static final TwdOdometry INSTANCE = new RimoTwdOdometry();

  // ---
  private RimoTwdOdometry() {
    super(RimoAxleConfiguration.rear());
  }

  /***************************************************/
  /** @param rimoGetEvent
   * @return velocity of the gokart projected to the x-axis in unit "m*s^-1"
   * computed from the angular rates of the rear wheels. The odometry value
   * has error due to slip. */
  public static Scalar tangentSpeed(RimoGetEvent rimoGetEvent) {
    return INSTANCE.tangentSpeed(rimoGetEvent.getAngularRate_Y_pair());
  }

  /** @param rimoGetEvent
   * @return rotational rate of the gokart (around z-axis) in unit "s^-1"
   * computed from the angular rates of the rear wheels. The odometry value
   * has error due to slip. */
  public static Scalar turningRate(RimoGetEvent rimoGetEvent) {
    return INSTANCE.turningRate(rimoGetEvent.getAngularRate_Y_pair());
  }
}
