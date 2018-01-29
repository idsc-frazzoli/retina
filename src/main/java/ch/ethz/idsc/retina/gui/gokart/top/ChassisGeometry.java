// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.io.Serializable;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class ChassisGeometry implements Serializable {
  public static final ChassisGeometry GLOBAL = AppResources.load(new ChassisGeometry());
  /***************************************************/
  /** distance from rear to front axle [m] */
  public Scalar xAxleRtoF = Quantity.of(+1.19, SI.METER);
  /** from center to outer protection boundary */
  public Scalar yHalfWidth = Quantity.of(0.7, SI.METER);
  /** distance from x-axis to front tire */
  public Scalar yTireFront = Quantity.of(0.48, SI.METER);
  /** distance from x-axis to front tire */
  public Scalar yTireRear = Quantity.of(0.54, SI.METER);
  /** approx. radius of tire when on gokart is on ground */
  public Scalar tireRadiusFront = Quantity.of(0.23 * 0.5, "m*rad^-1");
  public Scalar tireRadiusRear = Quantity.of(0.240 * 0.5, "m*rad^-1");
  /** front tire half width */
  public Scalar tireHalfWidthFront = Quantity.of(0.065, SI.METER);
  public Scalar tireHalfWidthContactFront = Quantity.of(0.045, SI.METER);
  /** rear tire half width */
  public Scalar tireHalfWidthRear = Quantity.of(0.0975, SI.METER);
  public Scalar tireHalfWidthContactRear = Quantity.of(0.0675, SI.METER);
  /***************************************************/
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.SI().in(SI.METER);

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

  public DifferentialSpeed getDifferentialSpeed() {
    return DifferentialSpeed.fromSI(xAxleDistanceMeter(), yTireRearMeter());
  }

  /** @param rate without unit */
  public Scalar steerAngleForTurningRatio(Scalar rate) {
    return Quantity.of(ArcTan.of(xAxleDistanceMeter().multiply(rate)), "rad");
  }
}
