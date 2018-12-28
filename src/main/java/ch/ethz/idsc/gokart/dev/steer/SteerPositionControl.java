// code by edo and jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** initially a PD controller was used
 * however, when put on the ground, the PD controller was insufficient to reach
 * target position.
 * 
 * therefore an integration part was added with anti-windup.
 * 
 * controller is specific for steering on gokart
 * 
 * Edo established in MATLAB the following constants
 * bw = {25, 30, 35, 40, 50}; in "rad/s"
 * Ki = {0.71, 1.23, 1.95, 2.900, 5.7};
 * Kp = {1.80, 2.59, 3.53, 4.612, 7.2};
 * Kd = {0.41, 0.49, 0.57, 0.655, 0.82};
 * for all variants: 80[deg] phase margin(?)
 * 
 * TODO interpolate PID constants depending on speed */
public class SteerPositionControl {
  static final Scalar DT = SteerSocket.INSTANCE.getPutPeriod();
  private final SteerConfig steerConfig;
  // ---
  /** pos error initially incorrect in the first iteration */
  private Scalar lastPos_error = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar lastTor_value = Quantity.of(0, SteerPutEvent.UNIT_RTORQUE); // unit "N*m"
  private Scalar lastIPt_value = Quantity.of(0, SteerPutEvent.UNIT_RTORQUE); // unit "N*m"

  public SteerPositionControl() {
    steerConfig = SteerConfig.GLOBAL;
  }

  public SteerPositionControl(SteerConfig steerConfig) {
    this.steerConfig = steerConfig;
  }

  /** @param pos_error in "SCE"
   * @return "N*m" */
  public Scalar iterate(Scalar pos_error) {
    final Scalar pPart = pos_error.multiply(steerConfig.Kp); // (e[k]-e[k-1])*Kp
    // ---
    final Scalar dPart = pos_error.subtract(lastPos_error).multiply(steerConfig.Kd).divide(DT);
    // ---
    final Scalar iPart = lastIPt_value.add(pos_error.multiply(steerConfig.Ki).multiply(DT)); // e*Ki*dt
    // ---
    lastPos_error = pos_error; // update for next iteration
    // ---
    Scalar testValue = pPart.add(dPart).add(iPart);
    Clip clip = steerConfig.torqueLimitClip();
    if (clip.isInside(testValue))
      lastIPt_value = iPart;
    lastTor_value = steerConfig.torqueLimitClip().apply(testValue); // anti-windup and update for next iteration
    return lastTor_value;
  }

  /** estimate steering speed */
  private Scalar lastPos_value = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);

  /** @param currentPos E"
   * @param wantedPos in "SCE"
   * @param wantedSpeed in "SCE s^-1"
   * @return "N*m" */
  public Scalar iterate(Scalar currentPos, Scalar wantedPos, Scalar wantedSpeed) {
    final Scalar pos_error = wantedPos.subtract(currentPos);
    final Scalar pPart = pos_error.multiply(steerConfig.Kp); // (e[k]-e[k-1])*Kp
    // ---
    final Scalar currentSpd = currentPos.subtract(lastPos_value).divide(DT);
    final Scalar vel_error = wantedSpeed.subtract(currentSpd);
    final Scalar dPart = vel_error.multiply(steerConfig.Kd);
    // ---
    final Scalar iPart = lastIPt_value.add(pos_error.multiply(steerConfig.Ki).multiply(DT)); // e*Ki*dt
    // ---
    lastPos_value = currentPos;
    // ---
    Scalar testValue = pPart.add(dPart).add(iPart);
    Clip clip = steerConfig.torqueLimitClip();
    if (clip.isInside(testValue))
      lastIPt_value = iPart;
    lastTor_value = steerConfig.torqueLimitClip().apply(testValue); // anti-windup and update for next iteration
    return lastTor_value;
  }
}
