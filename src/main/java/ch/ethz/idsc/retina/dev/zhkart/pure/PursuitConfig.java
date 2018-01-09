// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class PursuitConfig implements Serializable {
  public static final PursuitConfig GLOBAL = AppResources.load(new PursuitConfig());
  /***************************************************/
  public Scalar updatePeriod = Quantity.of(0.1, "s"); // 0.1[s] == 10[Hz]
  /** look ahead distance for pure pursuit controller
   * 20171218: changed from 2.8[m] to 3.5[m] otherwise tracked angle is out of range too frequently */
  public Scalar lookAhead = Quantity.of(3.5, "m");
  /** gokart velocity speed for curve follower module */
  public Scalar rateFollower = Quantity.of(16.0, "rad*s^-1");
  public Scalar poseQualityMin = RealScalar.of(0.1);
  /***************************************************/
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.SI().in(Unit.of("m"));

  public Scalar lookAheadMeter() {
    return TO_METER.apply(lookAhead);
  }

  public boolean isQualitySufficient(Scalar quality) {
    return Scalars.lessThan(poseQualityMin, quality);
  }
}
