// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.io.Serializable;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class PursuitConfig implements Serializable {
  /***************************************************/
  public final Scalar updatePeriod = Quantity.of(0.1, SI.SECOND); // 0.1[s] == 10[Hz]
  @FieldSubdivide(start = "0[m]", end = "10[m]", intervals = 20)
  public Scalar lookAhead = Quantity.of(3.5, SI.METER);
  /** gokart velocity speed for curve follower module
   * 20180531 the rate was increased to 75[s^-1]
   * 20180604 the rate was decreased to 50[s^-1] because of the presence of the tents */
  @FieldSubdivide(start = "30[s^-1]", end = "70[s^-1]", intervals = 4)
  public Scalar rateFollower = Quantity.of(50.0, SI.PER_SECOND);
}
