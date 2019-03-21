// code by mh, jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum DriftRatio {
  ;
  private static final Scalar MIN_DRIFT_VELOCITY = Quantity.of(0.5, SI.VELOCITY);

  /** @param velocityXY
   * @return unitless */
  public static Scalar of(Tensor velocityXY) {
    return Scalars.lessThan(velocityXY.Get(0).abs(), MIN_DRIFT_VELOCITY) //
        ? RealScalar.ZERO
        : velocityXY.Get(1).divide(velocityXY.Get(0));
  }
}
