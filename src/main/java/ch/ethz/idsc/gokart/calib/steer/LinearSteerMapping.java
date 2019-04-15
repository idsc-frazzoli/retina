// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.util.Objects;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** the linear steer mapping was in use from 2017-12 until at least 2018-09 */
public enum LinearSteerMapping implements SteerMapping {
  /** conversion factor from measured steer column angle to front wheel angle */
  INSTANCE(Quantity.of(0.6, "SCE^-1"));
  // ---
  private final Scalar column2steer;

  private LinearSteerMapping(Scalar column2steer) {
    this.column2steer = Objects.requireNonNull(column2steer);
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    return getAngleFromSCE(steerColumnInterface.getSteerColumnEncoderCentered());
  }

  @Override // from SteerMapping
  public Scalar getAngleFromSCE(Scalar scalar) {
    return scalar.multiply(column2steer);
  }

  @Override // from SteerMapping
  public Scalar getSCEfromAngle(Scalar angle) {
    return angle.divide(column2steer);
  }
}
