// code by mh, jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum DriftRatio {
  ;
  private static final Scalar MIN_DRIFT_VELOCITY = Quantity.of(0.5, SI.VELOCITY);

  /** @param velocityXY {vx[m*s^-1], vy[m*s^-1]}
   * @return unitless */
  public static Scalar of(Tensor velocityXY) {
    Scalar ux = velocityXY.Get(0);
    return Scalars.lessThan(ux.abs(), MIN_DRIFT_VELOCITY) //
        ? RealScalar.ZERO
        : velocityXY.Get(1).divide(ux);
  }
}
