// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/**  */
public class SafetyConfig implements Serializable {
  public static final SafetyConfig GLOBAL = AppResources.load(new SafetyConfig());
  /***************************************************/
  /** obstacles on path within clearance range may cause
   * gokart to deactivate motor torque
   * 20171218: changed from 3.3[m] to 4.3[m]
   * @see Vlp16ClearanceModule
   * @see Urg04lxClearanceModule */
  public Scalar clearanceFront = Quantity.of(4.3, SI.METER);
  /** TODO clearance rear is not yet used */
  public Scalar clearanceRear = Quantity.of(-2.2, SI.METER);
  /** 20180226: changed from -1.0[m] to -0.9[m] because the sensor rack was lowered by ~8[cm] */
  public Scalar vlp16_ZLo = Quantity.of(-0.9, SI.METER);
  public Scalar vlp16_ZHi = Quantity.of(+0.1, SI.METER);

  /***************************************************/
  public Scalar clearanceFrontMeter() {
    return Magnitude.METER.apply(clearanceFront);
  }

  public Scalar vlp16_ZLoMeter() {
    return Magnitude.METER.apply(vlp16_ZLo);
  }

  public Scalar vlp16_ZHiMeter() {
    return Magnitude.METER.apply(vlp16_ZHi);
  }
}
