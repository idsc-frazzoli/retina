// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.owl.car.model.CarControl;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface VehicleModel {
  /** @return mass [kg] */
  Scalar mass();

  /** @return number of tires */
  int wheels();

  /** @param index
   * @return description of wheel of given index */
  WheelInterface wheel(int index);

  /** @param delta steering angle
   * @return angles of wheels (measured from longitude forward direction)
   * for instance if the car has 4 wheels and traditional steering then
   * angles(delta) = {~delta, ~delta, 0, 0} */
  Tensor angles(Scalar delta);

  /** @return sequence of points in ccw direction that circumscribe the
   * footprint of the vehicle */
  Tensor footprint();

  /** @return inverse of yawing moment of inertia [kgm2] */
  Scalar Iz_invert();

  /** @param tensor with relative control parameters in range [-1,1], or [0,1]
   * @return control with absolute physical values */
  CarControl createControl(Tensor tensor);

  Scalar coulombFriction(Scalar speed);

  /** @return Nm per Mpa conversion constant [Nm/Mpa] for Front brakes */
  Scalar press2torF();

  /** @return Nm per Mpa conversion constant [Nm/Mpa] for Rear brakes */
  Scalar press2torR();

  Scalar muRoll();
}
