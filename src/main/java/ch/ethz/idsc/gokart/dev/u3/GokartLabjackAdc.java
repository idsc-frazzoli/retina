// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum GokartLabjackAdc {
  /** 0.3[V] when not pressed, 2.45[V]
   * 1.1[V] when not pressed, 5.11[V] */
  BOOST(Quantity.of(4.5, SI.VOLT)), //
  /** 1.1[V] when not pressed, 5.11[V] */
  REVERSE(Quantity.of(4.5, SI.VOLT)), //
  THROTTLE(null), //
  AUTONOMOUS(Quantity.of(7, SI.VOLT)), //
  /** not connected */
  _NC4(null), //
  ;
  private final Scalar threshold;

  private GokartLabjackAdc(Scalar threshold) {
    this.threshold = threshold;
  }

  /** @param allADC
   * @return whether threshold is less than voltage value on adc channel */
  public boolean isPressed(Tensor allADC) {
    return Scalars.lessThan(threshold, allADC.Get(ordinal()));
  }
}
