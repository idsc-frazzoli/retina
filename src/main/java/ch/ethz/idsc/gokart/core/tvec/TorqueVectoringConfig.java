// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Times;

/** parameters for PI controller of torque control
 * 
 * Reference: Marc Heim Thesis, p. 14 */
public class TorqueVectoringConfig {
  public static final TorqueVectoringConfig GLOBAL = AppResources.load(new TorqueVectoringConfig());
  /***************************************************/
  /** The Static compensation coefficient */
  public Scalar staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
  /** The Dynamic correction coefficient */
  public Scalar dynamicCorrection = Quantity.of(1.5, SI.SECOND);
  /** The Predictive correction coefficient */
  public Scalar staticPrediction = Quantity.of(0.0, SI.ANGULAR_ACCELERATION.negate());
  /** TODO document control constant used in ITV */
  public Scalar ks = Quantity.of(10.0, SI.SECOND);
  /** Scaling factor for Normalized torque vectoring */
  // TODO MH not used
  public Scalar kn = Quantity.of(1, SI.ACCELERATION.negate());
  /** ratio:
   * 0 means 100% old value
   * 1 means 100% new value
   * 0.5 means average */
  public Scalar rollingAverageRatio = RealScalar.of(0.1);

  /***************************************************/
  /** Reference: Marc Heim Thesis p. 14, eqs 2.32 and 2.35.
   * Eq 2.35 contains a typo: the factor v_x is missing.
   * However, the implementation is correct.
   * 
   * @param angularSlip [s^-1]
   * @return dynamic component unitless */
  private final Scalar getDynamicComponent(AngularSlip angularSlip) {
    return angularSlip.angularSlip().multiply(dynamicCorrection);
  }

  /** Reference: Marc Heim Thesis p. 14, eqs 2.31 and 2.34
   * 
   * @param angularSlip
   * @return unitless */
  private final Scalar getStaticComponent(AngularSlip angularSlip) {
    Scalar tangentSpeed = angularSlip.tangentSpeed();
    return Times.of( //
        angularSlip.rotationPerMeterDriven(), //
        tangentSpeed, //
        tangentSpeed, //
        staticCompensation);
  }

  /** @param angularSlip
   * @return unitless */
  public final Scalar getDynamicAndStatic(AngularSlip angularSlip) {
    return getDynamicComponent(angularSlip).add(getStaticComponent(angularSlip));
  }

  /** @param expectedRotationAcceleration
   * @return unitless */
  public final Scalar getPredictiveComponent(Scalar expectedRotationAcceleration) {
    return expectedRotationAcceleration.multiply(staticPrediction);
  }
}
