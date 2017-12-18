// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
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

  private PursuitConfig() {
  }

  /***************************************************/
  public Scalar updatePeriod = Quantity.of(0.2, "s");
  /** look ahead distance for pure pursuit controller */
  public Scalar lookAhead = Quantity.of(2.0, "m");
  /** rate for curve follower module */
  public Scalar rateFollower = Quantity.of(8.0, "rad*s^-1");
  /***************************************************/
  private static final ScalarUnaryOperator TO_SECONDS = QuantityMagnitude.SI().in(Unit.of("s"));
  private static final ScalarUnaryOperator TO_METER = QuantityMagnitude.SI().in(Unit.of("m"));

  public Scalar updatePeriodSeconds() {
    return TO_SECONDS.apply(updatePeriod);
  }

  public Scalar lookAheadMeter() {
    return TO_METER.apply(lookAhead);
  }
}
