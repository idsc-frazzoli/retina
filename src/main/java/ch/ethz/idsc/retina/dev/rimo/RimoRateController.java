// concept by jelavice 
// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class RimoRateController {
  private final Scalar dt = Quantity.of(RimoSocket.SEND_PERIOD_MS * 1e-3, "s");
  /** pos error initially incorrect in the first iteration */
  private Scalar lastVel_error = Quantity.of(0, RimoGetTire.UNIT_RATE);
  private Scalar lastTor_value = Quantity.of(0, RimoPutTire.UNIT_TORQUE);

  /** @param vel_error
   * @return */
  public Scalar iterate(Scalar vel_error) {
    Scalar pPart = vel_error.subtract(lastVel_error).multiply(RimoConfig.GLOBAL.Kp);
    Scalar iPart = vel_error.multiply(RimoConfig.GLOBAL.Ki).multiply(dt);
    lastVel_error = vel_error;
    Scalar tor_value = lastTor_value.add(pPart).add(iPart);
    lastTor_value = RimoConfig.GLOBAL.torqueLimitClip().apply(tor_value); // anti-windup
    return lastTor_value;
  }
}
