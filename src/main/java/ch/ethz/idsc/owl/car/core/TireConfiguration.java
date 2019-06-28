// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** information about tire
 * 
 * @see WheelConfiguration for the information of where the wheel is attached */
// TODO JPH move to package gokart.X
public interface TireConfiguration {
  /** @return radius of tire effective for odometry with unit [m] */
  Scalar radius();

  /** @return half width with unit [m] */
  Scalar halfWidth();

  /** @return polygon for drawing tire shape top view */
  Tensor footprint();
}
