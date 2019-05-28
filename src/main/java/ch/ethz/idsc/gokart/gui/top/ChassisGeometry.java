// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.core.mpc.MPCOptimizationConfig;
import ch.ethz.idsc.owl.car.math.DifferentialSpeed;
import ch.ethz.idsc.owl.car.math.TurningGeometry;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.ArcTan;

/** parameters in this config class are final because they
 * correspond to immutable characteristic of the gokart.
 * 
 * further constants can be found in {@link MPCOptimizationConfig} */
// TODO JPH location of class not good
public class ChassisGeometry {
  public static final ChassisGeometry GLOBAL = AppResources.load(new ChassisGeometry());
  /***************************************************/
  /** distance from rear to front axle [m] */
  public final Scalar xAxleRtoF = Quantity.of(+1.19, SI.METER);
  /** distance from front axle to front tip of gokart [m] */
  public final Scalar xAxleFtoTip = Quantity.of(+0.56, SI.METER);
  /** from center to outer protection boundary along y-axis */
  public final Scalar yHalfWidth = Quantity.of(0.7, SI.METER);
  /** distance from x-axis to front tire */
  public final Scalar yTireFront = Quantity.of(0.48, SI.METER);
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
    return Magnitude.METER.apply(xAxleRtoF);
  }

  public Scalar xTipMeter() {
    return Total.of(Tensors.of(xAxleRtoF, xAxleFtoTip).map(Magnitude.METER)).Get();
  }

  public Scalar yTireFrontMeter() {
    return Magnitude.METER.apply(yTireFront);
  }

  public DifferentialSpeed getDifferentialSpeed() {
    Scalar yTireRear = RimoAxleConfiguration.rear().wheel(0).local().Get(1);
    return DifferentialSpeed.fromSI(xAxleRtoF, yTireRear);
  }

  /** function ArcTan[d * r] approx. d * r for d ~ 1 and small r
   * inverse function of {@link TurningGeometry}
   * 
   * @param ratio [m^-1] see for instance SteerConfig.GLOBAL.turningRatioMax
   * @return steering angle unitless */
  public Scalar steerAngleForTurningRatio(Scalar ratio) {
    return ArcTan.of(xAxleRtoF.multiply(ratio));
  }
}
