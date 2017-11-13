// concept by jelavice 
// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PIRimoRateController {
  private final Scalar dt = Quantity.of(RimoSocket.SEND_PERIOD_MS * 1e-3, "s");
  /** pos error initially incorrect in the first iteration */
  private Scalar lastVel_error = Quantity.of(0, RimoGetTire.RATE_UNIT);
  private Scalar lastTor_value = Quantity.of(0, RimoGetTire.RATE_UNIT);

  /** @param vel_error
   * @return */
  public Scalar iterate(Scalar vel_error) { // "e"
    Scalar pPart = vel_error.subtract(lastVel_error).multiply(RimoConfig.GLOBAL.Kp);
    Scalar iPart = vel_error.multiply(RimoConfig.GLOBAL.Ki).multiply(dt);
    lastVel_error = vel_error;
    Scalar tor_value = lastTor_value.add(pPart).add(iPart);
    lastTor_value = tor_value;
    // FIXME anti-windup missing
    return RimoConfig.GLOBAL.torqueLimitClip().apply(tor_value);
  }
}
