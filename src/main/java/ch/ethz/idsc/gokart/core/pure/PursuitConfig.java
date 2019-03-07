// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class PursuitConfig {
  public static final PursuitConfig GLOBAL = AppResources.load(new PursuitConfig());
  /***************************************************/
  public final Scalar updatePeriod = Quantity.of(0.1, SI.SECOND); // 0.1[s] == 10[Hz]
  /** look ahead distance for pure pursuit controller
   * 20171218: changed from 2.8[m] to 3.5[m] otherwise tracked angle is out of range too frequently
   * 20180304: changed from 3.5[m] to 3.9[m] to match with value used many times before
   * 20180929: changed from 3.9[m] to 3.5[m]
   * TODO as look ahead as decreased -> increase pure pursuit update rate also */
  @FieldSubdivide(start = "2.5[m]", end = "4[m]", intervals = 6)
  public Scalar lookAhead = Quantity.of(3.5, SI.METER);
  /** gokart velocity speed for curve follower module
   * 20180531 the rate was increased to 75[rad*s^-1]
   * 20180604 the rate was decreased to 50[rad*s^-1] because of the presence of the tents */
  @FieldSubdivide(start = "30[rad*s^-1]", end = "70[rad*s^-1]", intervals = 4)
  public Scalar rateFollower = Quantity.of(50.0, "rad*s^-1");
  /** poseQualityMin is threshold above which a pose quality is considered sufficient */
  public final Scalar poseQualityMin = RealScalar.of(0.5);

  /***************************************************/
  /** @return unitless look ahead distance with interpretation in meters */
  public Scalar lookAheadMeter() {
    return Magnitude.METER.apply(lookAhead);
  }

  /** @param quality in the interval [0, 1]
   * @return if given pose quality is considered sufficient to qualify as valid */
  public boolean isQualitySufficient(Scalar quality) {
    return Scalars.lessThan(poseQualityMin, quality);
  }
}
