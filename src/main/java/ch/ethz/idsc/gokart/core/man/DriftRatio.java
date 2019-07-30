// code by mh, jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum DriftRatio implements TensorScalarFunction {
  INSTANCE;
  // ---
  private static final Scalar MIN_DRIFT_VELOCITY = Quantity.of(0.5, SI.VELOCITY);

  /** @param velocity {vx[m*s^-1], vy[m*s^-1], ...}
   * @return unitless */
  @Override
  public Scalar apply(Tensor velocity) {
    Scalar ux = velocity.Get(0);
    return Scalars.lessThan(ux.abs(), MIN_DRIFT_VELOCITY) //
        ? RealScalar.ZERO
        : velocity.Get(1).divide(ux);
  }
}
