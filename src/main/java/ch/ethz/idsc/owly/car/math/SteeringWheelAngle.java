// code by jph
package ch.ethz.idsc.owly.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Tan;

/** formula to convert steering angle to (front-)wheel angle
 * so that no friction arises in the ideal/no-slip scenario.
 * 
 * formula simplified from document by marcello and panos
 * 
 * see also
 * <a href="https://en.wikipedia.org/wiki/Ackermann_steering_geometry">Ackermann steering geometry</a> */
@Deprecated
public enum SteeringWheelAngle {
  ;
  /** (px, py) is front wheel position measured from center of rear axle in local car coordinates
   * 
   * px is typically positive
   * |py| is typically less than px
   * 
   * @param ratio == py / px
   * @param delta steering angle
   * @return result typically close to delta */
  public static Scalar of(Scalar ratio, Scalar delta) {
    Scalar tan = Tan.of(delta);
    return ArcTan.of(tan.divide(RealScalar.ONE.add(ratio.multiply(tan))));
  }
}
