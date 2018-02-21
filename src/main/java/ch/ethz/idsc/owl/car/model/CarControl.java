// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** controls in absolute physical magnitude */
public class CarControl {
  public final Scalar delta; // [rad], access via functions ...angles()
  public final Scalar brake; // non-negative
  public final Scalar handbrake; // non-negative
  /** vector with throttle for each tire */
  public final Tensor throttleV; // non-negative

  /** constructor is invoked in {@link VehicleModel#createControl(Tensor)}
   * 
   * @param delta
   * @param brake
   * @param handbrake
   * @param throttleV */
  public CarControl(Scalar delta, Scalar brake, Scalar handbrake, Tensor throttleV) {
    this.delta = delta;
    this.brake = brake;
    this.handbrake = handbrake;
    this.throttleV = throttleV.unmodifiable();
  }
}
