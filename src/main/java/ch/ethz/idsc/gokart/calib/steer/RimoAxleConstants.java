// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.bot.se2.AckermannSteering;
import ch.ethz.idsc.owl.bot.se2.DifferentialSpeed;
import ch.ethz.idsc.owl.bot.se2.TurningGeometry;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ArcTan;

public enum RimoAxleConstants {
  ;
  /** distance from rear to front axle [m] */
  public static final Scalar xAxleRtoF = Quantity.of(+1.19, SI.METER);
  /** distance from x-axis to front tire */
  public static final Scalar yTireFront = Quantity.of(0.48, SI.METER);
  /** */
  public static final Scalar yTireRear = Quantity.of(+0.54, SI.METER);

  public static DifferentialSpeed getDifferentialSpeed() {
    return DifferentialSpeed.fromSI(xAxleRtoF, yTireRear);
  }

  /** function ArcTan[d * r] approx. d * r for d ~ 1 and small r
   * inverse function of {@link TurningGeometry}
   * 
   * @param ratio [m^-1] see for instance SteerConfig.GLOBAL.turningRatioMax
   * @return steering angle unitless */
  public static Scalar steerAngleForTurningRatio(Scalar ratio) {
    return ArcTan.of(xAxleRtoF.multiply(ratio));
  }

  public static AckermannSteering ackermannSteering() {
    return new AckermannSteering(xAxleRtoF, yTireFront);
  }
}
