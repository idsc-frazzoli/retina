// concept by jelavice 
// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class PIRimoRateController {
  private final Scalar dt = RealScalar.of(RimoSocket.SEND_PERIOD_MS * 1e-3);
  /** pos error initially incorrect in the first iteration */
  private Scalar lastVel_error = RealScalar.ZERO;
  private Scalar lastTor_value = RealScalar.ZERO;

  public Scalar iterate(Scalar vel_error) { // "e"
    Scalar pPart = vel_error.subtract(lastVel_error).multiply(TorqueConfig.GLOBAL.Kp);
    Scalar iPart = vel_error.multiply(TorqueConfig.GLOBAL.Ki).multiply(dt);
    lastVel_error = vel_error;
    Scalar tor_value = lastTor_value.add(pPart).add(iPart);
    lastTor_value = tor_value;
    // FIXME anti-windup missing
    return TorqueConfig.GLOBAL.torqueLimitClip().apply(tor_value);
  }
}
