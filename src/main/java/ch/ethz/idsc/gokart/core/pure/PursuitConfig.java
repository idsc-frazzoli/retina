// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class PursuitConfig implements Serializable {
  public static final PursuitConfig GLOBAL = AppResources.load(new PursuitConfig());
  /***************************************************/
  public Scalar updatePeriod = Quantity.of(0.1, SI.SECOND); // 0.1[s] == 10[Hz]
  /** look ahead distance for pure pursuit controller
   * 20171218: changed from 2.8[m] to 3.5[m] otherwise tracked angle is out of range too frequently
   * 20180304: changed from 3.5[m] to 3.9[m] to match with value used many times before */
  public Scalar lookAhead = Quantity.of(3.9, SI.METER);
  /** gokart velocity speed for curve follower module */
  public Scalar rateFollower = Quantity.of(20.0, "rad*s^-1");
  public Scalar poseQualityMin = RealScalar.of(0.5);

  /***************************************************/
  public Scalar lookAheadMeter() {
    return Magnitude.METER.apply(lookAhead);
  }

  public boolean isQualitySufficient(Scalar quality) {
    return Scalars.lessThan(poseQualityMin, quality);
  }
}
