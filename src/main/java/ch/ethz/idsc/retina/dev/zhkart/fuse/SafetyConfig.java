// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/**  */
public class SafetyConfig implements Serializable {
  public static final SafetyConfig GLOBAL = AppResources.load(new SafetyConfig());

  private SafetyConfig() {
  }

  /***************************************************/
  public Scalar clearanceFront = Quantity.of(3.2, "m");
  public Scalar clearanceRear = Quantity.of(-2.2, "m");
  private static final ScalarUnaryOperator TOMETER = QuantityMagnitude.SI().in(Unit.of("m"));

  public Scalar clearanceFrontMeter() {
    return TOMETER.apply(clearanceFront);
  }
}
