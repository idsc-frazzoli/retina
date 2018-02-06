// concept by jelavice 
// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** two instance of this class are used for left and right rear motors
 * @see RimoRateControllerDuo
 * 
 * Kp with unit "ARMS*rad^-1*s"
 * Ki with unit "ARMS*rad^-1" */
/* package */ class RimoRateController {
  static final Scalar DT = RimoSocket.INSTANCE.getPutPeriod();
  // ---
  /** pos error initially incorrect in the first iteration */
  private Scalar lastVel_error = Quantity.of(0, RimoGetTire.UNIT_RATE); // unit "rad*s^-1"
  private Scalar lastTor_value = Quantity.of(0, RimoPutTire.UNIT_TORQUE); // unit "ARMS"

  /** @param vel_error with unit "rad*s^-1"
   * @return value with unit "ARMS" */
  public Scalar iterate(Scalar vel_error) {
    Scalar pPart = vel_error.subtract(lastVel_error).multiply(RimoConfig.GLOBAL.Kp);
    Scalar iPart = vel_error.multiply(RimoConfig.GLOBAL.Ki).multiply(DT);
    lastVel_error = vel_error;
    Scalar tor_value = lastTor_value.add(pPart).add(iPart);
    lastTor_value = RimoConfig.GLOBAL.torqueLimitClip().apply(tor_value); // anti-windup
    return lastTor_value;
  }
}
