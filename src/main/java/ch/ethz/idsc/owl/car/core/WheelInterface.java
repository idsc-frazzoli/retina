// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.owl.car.math.Pacejka3;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface WheelInterface {
  /** @return lever from COG */
  Tensor lever();

  /** @return radius of wheel [m] */
  Scalar radius();

  /** @return width of wheel on ground [m] */
  Scalar width();

  /** @return inverse of wheel moment of inertia [kgm2] */
  Scalar Iw_invert();

  Pacejka3 pacejka();
}
