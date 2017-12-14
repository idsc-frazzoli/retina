// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.io.Serializable;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class ChassisGeometry implements Serializable {
  public static final ChassisGeometry GLOBAL = AppResources.load(new ChassisGeometry());

  private ChassisGeometry() {
  }

  /***************************************************/
  /** distance from rear to front axle [m] */
  public Scalar xAxleRtoF = Quantity.of(+1.19, "m");
  /** from center to outer protection boundary */
  public Scalar yHalfWidth = Quantity.of(0.7, "m");
  /** distance from x-axis to front tire */
  public Scalar yTireFront = Quantity.of(0.48, "m");
  /** distance from x-axis to front tire */
  public Scalar yTireRear = Quantity.of(0.54, "m");
  /** approx. radius of tire when on gokart is on ground */
  public Scalar tireRadiusFront = Quantity.of(0.23 * 0.5, "m*rad^-1");
  public Scalar tireRadiusRear = Quantity.of(0.240 * 0.5, "m*rad^-1");
  /***************************************************/
  private static final ScalarUnaryOperator TOMETER = QuantityMagnitude.SI().in(Unit.of("m"));

  public Scalar yHalfWidthMeter() {
    return TOMETER.apply(yHalfWidth);
  }

  /** @return approximately 1.19 */
  public Scalar xAxleDistanceMeter() {
    return TOMETER.apply(xAxleRtoF);
  }

  public Scalar yTireRearMeter() {
    return TOMETER.apply(yTireRear);
  }

  public Scalar yTireFrontMeter() {
    return TOMETER.apply(yTireFront);
  }

  public DifferentialSpeed getDifferentialSpeed() {
    return DifferentialSpeed.fromSI(xAxleDistanceMeter(), yTireRearMeter());
  }
}
