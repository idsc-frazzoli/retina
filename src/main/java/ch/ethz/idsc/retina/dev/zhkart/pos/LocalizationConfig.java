// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/**  */
public class LocalizationConfig implements Serializable {
  public static final LocalizationConfig GLOBAL = AppResources.load(new LocalizationConfig());

  private LocalizationConfig() {
  }

  /***************************************************/
  public Scalar threshold = RealScalar.of(20.0);
  public Scalar resampleDs = RealScalar.of(1 / 3.0);
  /***************************************************/
  // private static final ScalarUnaryOperator TOMETER = QuantityMagnitude.SI().in(Unit.of("m"));
}
