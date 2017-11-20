// code by edo and jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** controller is specific for steering on gokart */
public class SteerPositionIDControl {
  private static final Tensor SECOND_ORDER = Tensors.vectorDouble(1, -2, 1);
  // ---
  private final Scalar dt = Quantity.of(SteerSocket.SEND_PERIOD_MS * 1e-3, "s");
  /** pos error initially incorrect in the first iteration */
  private Scalar las1Pos_error = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar las2Pos_error = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar lastTor_value = Quantity.of(0, SteerPutEvent.UNIT_TORQUE); // unit "N*m"

  /** @param pos_error in "SCE"
   * @return "N*m" */
  public Scalar iterate(Scalar pos_error) {
    final Scalar pPart = pos_error.subtract(las1Pos_error).multiply(SteerConfig.GLOBAL.Kp); // (e[k]-e[k-1])*Kp
    // ---
    Scalar d2Est = SECOND_ORDER.dot(Tensors.of(pos_error, las1Pos_error, las2Pos_error)).Get();
    final Scalar dPart = d2Est.multiply(SteerConfig.GLOBAL.Kd).divide(dt);
    // ---
    final Scalar iPart = pos_error.multiply(SteerConfig.GLOBAL.Ki).multiply(dt); // e*Ki*dt
    // ---
    las2Pos_error = las1Pos_error; // update for next iteration
    las1Pos_error = pos_error; // update for next iteration
    // ---
    Scalar tor_value = lastTor_value.add(pPart).add(dPart).add(iPart);
    lastTor_value = SteerConfig.GLOBAL.torqueLimitClip().apply(tor_value); // anti-windup and update for next iteration
    return lastTor_value;
  }
}
