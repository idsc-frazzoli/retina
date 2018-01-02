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
  /***************************************************/
  /** obstacles on path within clearance range may cause
   * gokart to deactivate motor torque
   * 20171218: changed from 3.3[m] to 4.3[m]
   * @see Vlp16ClearanceModule
   * @see Urg04lxClearanceModule */
  public Scalar clearanceFront = Quantity.of(4.3, "m");
  /** TODO clearance rear is not yet used */
  public Scalar clearanceRear = Quantity.of(-2.2, "m");
  public Scalar vlp16_ZLo = Quantity.of(-1.0, "m");
  public Scalar vlp16_ZHi = Quantity.of(+0.1, "m");
  /***************************************************/
  private static final ScalarUnaryOperator TOMETER = QuantityMagnitude.SI().in(Unit.of("m"));

  public Scalar clearanceFrontMeter() {
    return TOMETER.apply(clearanceFront);
  }

  public Scalar vlp16_ZLoMeter() {
    return TOMETER.apply(vlp16_ZLo);
  }

  public Scalar vlp16_ZHiMeter() {
    return TOMETER.apply(vlp16_ZHi);
  }
}
