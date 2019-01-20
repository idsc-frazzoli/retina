// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import java.util.Objects;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.time.IntervalClock;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ImprovedNormalizedPredictiveTorqueVectoring extends ImprovedNormalizedTorqueVectoring {
  private static final double MIN_DT = 0.000001;
  /** ratio:
   * 0 means 100% old value
   * 1 means 100% new value
   * 0.5 means average */
  private static final Scalar ROLLING_AVERAGE_RATIO = RealScalar.of(0.5); // good data expected
  private static final Scalar ROLLING_AVERAGE_VALUE = Quantity.of(0.0, SI.ANGULAR_ACCELERATION);
  // ---
  private final IntervalClock intervalClock = new IntervalClock();
  private final GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, ROLLING_AVERAGE_RATIO, ROLLING_AVERAGE_VALUE);

  public ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override // from ImprovedNormalizedTorqueVectoring
  public final Tensor getMotorCurrentsFromAcceleration( //
      Scalar expectedRotationPerMeterDriven, //
      Scalar meanTangentSpeed, //
      Scalar angularSlip, //
      Scalar wantedAcceleration, //
      Scalar realRotation) {
    Scalar expectedRotationVelocity = meanTangentSpeed.multiply(expectedRotationPerMeterDriven);
    Scalar expectedRoationAcceleration = estimateRotationAcceleration(expectedRotationVelocity, intervalClock.seconds());
    return getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation, //
        expectedRoationAcceleration);
  }

  private Scalar lastRotation = null;
  private Scalar rotationAcc_fallback = ROLLING_AVERAGE_VALUE;

  /** @param rotation [s^-1]
   * @param timeSinceLastStep
   * @return estimation of rotational acceleration with unit [s^-2] */
  /* package */ Scalar estimateRotationAcceleration(Scalar rotation, double timeSinceLastStep) {
    if (Objects.isNull(lastRotation))
      lastRotation = rotation;
    if (timeSinceLastStep >= MIN_DT) {
      Scalar instantRotChange = rotation.subtract(lastRotation).divide(Quantity.of(timeSinceLastStep, SI.SECOND));
      rotationAcc_fallback = geodesicIIR1Filter.apply(instantRotChange).Get();
    }
    lastRotation = rotation;
    return rotationAcc_fallback;
  }

  private Tensor getMotorCurrentsFromAcceleration( //
      Scalar expectedRotationPerMeterDriven, //
      Scalar meanTangentSpeed, //
      Scalar angularSlip, //
      Scalar wantedAcceleration, //
      Scalar realRotation, //
      Scalar expectedRotationAcceleration) {
    Scalar dynamicComponent = getDynamicComponent(angularSlip);
    Scalar staticComponent = getStaticComponent(expectedRotationPerMeterDriven, meanTangentSpeed);
    Scalar predictiveComponent = getPredictiveComponent(expectedRotationAcceleration);
    // ---
    Scalar wantedZTorque = wantedZTorque( //
        dynamicComponent.add(staticComponent).add(predictiveComponent), // One
        realRotation);
    // left and right power prefer power over Z-torque
    return getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, meanTangentSpeed);
  }

  private Scalar getPredictiveComponent(Scalar expectedRotationAcceleration) {
    return expectedRotationAcceleration.multiply(torqueVectoringConfig.staticPrediction);
  }
}
