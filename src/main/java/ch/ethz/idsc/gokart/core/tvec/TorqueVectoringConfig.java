// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.ref.FieldClip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

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
  /** ks is used for stabilization in {@link #wantedZTorque(Scalar, Scalar)} */
  public Scalar ks = Quantity.of(10.0, SI.SECOND);
  /** ratio:
   * 0 means 100% old value
   * 1 means 100% new value
   * 0.5 means average */
  @FieldClip(min = "0", max = "1")
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

  /** @param wantedZTorque
   * @param realRotation
   * @return stabilized wantedZTorque */
  public final Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    if (Sign.isNegative(realRotation.multiply(wantedZTorque))) {
      Scalar scalar = Clips.unit().apply(realRotation.abs().multiply(ks));
      Scalar stabilizerFactor = RealScalar.ONE.subtract(scalar);
      return wantedZTorque.multiply(stabilizerFactor);
    }
    return wantedZTorque;
  }
}
