// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum GokartLabjackAdc {
  /** "boost" button on steering wheel.
   * 1.35280[V] when not pressed
   * 5.15748[V] when pressed */
  BOOST(Quantity.of(4.5, SI.VOLT)), //
  /** "reverse" button on steering wheel
   * 1.32312[V] when not pressed
   * 5.17674[V] when pressed */
  REVERSE(Quantity.of(4.5, SI.VOLT)), //
  THROTTLE(null), //
  /** "autonomous" button next to driver
   * 1.30298[V] when not pressed
   * 10.2459[V] when pressed */
  AUTONOMOUS(Quantity.of(9.5, SI.VOLT)), //
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
