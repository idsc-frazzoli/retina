// code by edo and jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** IMPLEMENTATION CONTAINS INITIAL DRAFT FOR STEERING CONTROL ON GOKART
 * 
 * THE IMPLEMENTATION IS SUPERSEEDED BY PID CONTROLLER IN
 * {@link SteerPositionControl}
 * 
 * controller is specific for steering on gokart */
/* package */ class SteerSimplePositionControl {
  static final Scalar DT = SteerSocket.INSTANCE.getPutPeriod();
  // ---
  /** pos error initially incorrect in the first iteration */
  private Scalar lastPos_error = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);

  /** @param pos_error in "SCE"
   * @return */
  public Scalar iterate(Scalar pos_error) {
    Scalar pPart = pos_error.multiply(SteerConfig.GLOBAL.Kp);
    Scalar dPart = pos_error.subtract(lastPos_error).multiply(SteerConfig.GLOBAL.Kd).divide(DT);
    lastPos_error = pos_error;
    Scalar control = pPart.add(dPart);
    return SteerConfig.GLOBAL.torqueLimitClip().apply(control);
  }
}
