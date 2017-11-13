// code by edo and jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class PDSteerPositionControl {
  private final Scalar dt = RealScalar.of(SteerSocket.SEND_PERIOD_MS * 1e-3);
  /** pos error initially incorrect in the first iteration */
  private Scalar lastPos_error = RealScalar.ZERO;

  public Scalar iterate(Scalar pos_error) {
    Scalar pPart = pos_error.multiply(SteerConfig.GLOBAL.Kp);
    Scalar dPart = pos_error.subtract(lastPos_error).multiply(SteerConfig.GLOBAL.Kd).divide(dt);
    lastPos_error = pos_error;
    Scalar control = pPart.add(dPart);
    return SteerConfig.GLOBAL.torqueLimitClip().apply(control);
  }
}
