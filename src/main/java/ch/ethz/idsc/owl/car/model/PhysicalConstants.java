// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum PhysicalConstants {
  ;
  public static final Scalar G_EARTH = Quantity.of(9.81, SI.ACCELERATION);
}
