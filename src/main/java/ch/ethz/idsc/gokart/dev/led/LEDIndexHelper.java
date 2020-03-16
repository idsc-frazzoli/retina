// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;

public enum LEDIndexHelper {
  ;

  public static int getIn(Number value, Clip range) {
    return getIn(RealScalar.of(value), range);
  }

  public static int getIn(Scalar value, Clip range) {
    double normed = range.rescale(value.negate()).number().doubleValue();
    return (int) Math.round(normed * (LEDStatus.NUM_LEDS - 1));
  }
}
