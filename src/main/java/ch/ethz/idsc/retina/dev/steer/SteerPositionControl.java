// code by edo and jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** initially a PD controller was used
 * however, when put on the ground, the PD controller was insufficient to reach
 * target position.
 * 
 * therefore an integration part was added with anti-windup.
 * 
 * controller is specific for steering on gokart */
public class SteerPositionControl {
  private static final Scalar DT = Quantity.of(SteerSocket.INSTANCE.getPeriod() * 1e-3, "s");
  // ---
  /** pos error initially incorrect in the first iteration */
  private Scalar lastPos_error = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar lastTor_value = Quantity.of(0, SteerPutEvent.UNIT_RTORQUE); // unit "N*m"
  private Scalar lastIPt_value = Quantity.of(0, SteerPutEvent.UNIT_RTORQUE); // unit "N*m"

  /** @param pos_error in "SCE"
   * @return "N*m" */
  public Scalar iterate(Scalar pos_error) {
    final Scalar pPart = pos_error.multiply(SteerConfig.GLOBAL.Kp); // (e[k]-e[k-1])*Kp
    // ---
    final Scalar dPart = pos_error.subtract(lastPos_error).multiply(SteerConfig.GLOBAL.Kd).divide(DT);
    // ---
    final Scalar iPart = lastIPt_value.add(pos_error.multiply(SteerConfig.GLOBAL.Ki).multiply(DT)); // e*Ki*dt
    // ---
    lastPos_error = pos_error; // update for next iteration
    // ---
    Scalar testValue = pPart.add(dPart).add(iPart);
    Clip clip = SteerConfig.GLOBAL.torqueLimitClip();
    if (clip.isInside(testValue))
      lastIPt_value = iPart;
    lastTor_value = SteerConfig.GLOBAL.torqueLimitClip().apply(testValue); // anti-windup and update for next iteration
    return lastTor_value;
  }
}
