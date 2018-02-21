// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/**  */
public class LocalizationConfig implements Serializable {
  public static final LocalizationConfig GLOBAL = AppResources.load(new LocalizationConfig());
  /***************************************************/
  public Scalar threshold = RealScalar.of(33.0);
  public Scalar resampleDs = RealScalar.of(0.4);

  /***************************************************/
  // private static final ScalarUnaryOperator TOMETER = QuantityMagnitude.SI().in(Unit.of("m"));
  public ParametricResample getUniformResample() {
    return new ParametricResample(threshold, resampleDs);
  }
}
