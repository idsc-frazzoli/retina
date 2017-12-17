// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.io.Serializable;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ArcTan;
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
  // TODO DUBENDORF measure
  public Scalar tireHalfWidthFront = Quantity.of(0.07, "m");
  public Scalar tireHalfWidthRear = Quantity.of(0.09, "m");
  /***************************************************/
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.SI().in(Unit.of("m"));

  public Scalar yHalfWidthMeter() {
    return TO_METER.apply(yHalfWidth);
  }

  /** @return approximately 1.19 */
  public Scalar xAxleDistanceMeter() {
    return TO_METER.apply(xAxleRtoF);
  }

  public Scalar yTireRearMeter() {
    return TO_METER.apply(yTireRear);
  }

  public Scalar yTireFrontMeter() {
    return TO_METER.apply(yTireFront);
  }

  public Scalar tireHalfWidthFront() {
    return TO_METER.apply(tireHalfWidthFront);
  }

  public Scalar tireHalfWidthRear() {
    return TO_METER.apply(tireHalfWidthRear);
  }

  /** @param lookAhead {x, y} without units
   * @return quantity with unit "rad" */
  public Scalar steerAngleTowards(Tensor lookAhead) {
    Scalar frontAxle_x = lookAhead.Get(0).subtract(xAxleDistanceMeter());
    Scalar frontAxle_y = lookAhead.Get(1);
    return Quantity.of(ArcTan.of(frontAxle_x, frontAxle_y), "rad");
  }

  public DifferentialSpeed getDifferentialSpeed() {
    return DifferentialSpeed.fromSI(xAxleDistanceMeter(), yTireRearMeter());
  }
}
