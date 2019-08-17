// code by jph
package ch.ethz.idsc.gokart.calib;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConstants;
import ch.ethz.idsc.gokart.core.mpc.MPCOptimizationConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;

/** parameters in this config class are final because they
 * correspond to immutable characteristic of the gokart.
 * 
 * further constants can be found in {@link MPCOptimizationConfig} */
public class ChassisGeometry {
  public static final ChassisGeometry GLOBAL = AppResources.load(new ChassisGeometry());
  /***************************************************/
  /** distance from front axle to front tip of gokart [m] */
  public final Scalar xAxleFtoTip = Quantity.of(+0.56, SI.METER);
  /** from center to outer protection boundary along y-axis */
  public final Scalar yHalfWidth = Quantity.of(0.7, SI.METER);
  /** front tire half width contact */
  public final Scalar tireHalfWidthContactFront = Quantity.of(0.045, SI.METER);
  /** rear tire half width contact */
  public final Scalar tireHalfWidthContactRear = Quantity.of(0.0675, SI.METER);
  /** approximation of ground clearance measured on 20180507 */
  public final Scalar groundClearance = Quantity.of(0.03, SI.METER);
  /** longitudinal distance to center of mass from back axle */
  public final Scalar xAxleRtoCoM = Quantity.of(0.46, SI.METER);

  /***************************************************/
  public Scalar yHalfWidthMeter() {
    return Magnitude.METER.apply(yHalfWidth);
  }

  /** @return approximately 1.19 */
  public Scalar xAxleDistanceMeter() {
    return Magnitude.METER.apply(RimoAxleConstants.xAxleRtoF);
  }

  public Scalar xTipMeter() {
    return Total.of(Tensors.of(RimoAxleConstants.xAxleRtoF, xAxleFtoTip).map(Magnitude.METER)).Get();
  }

  public Scalar yTireFrontMeter() {
    return Magnitude.METER.apply(RimoAxleConstants.yTireFront);
  }
}
