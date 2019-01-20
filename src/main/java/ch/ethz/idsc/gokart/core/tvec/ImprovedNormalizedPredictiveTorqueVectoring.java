// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.time.IntervalClock;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ImprovedNormalizedPredictiveTorqueVectoring extends ImprovedNormalizedTorqueVectoring {
  private static final double MIN_DT = 0.000001;
  private static final Scalar ROLLING_AVERAGE_FACTOR = RealScalar.of(0.5); // good data expected
  // ---
  private final IntervalClock intervalClock = new IntervalClock();
  private Scalar lastRotation = null;
  // TODO JPH/MH extract functionality to separate class "IIR filter"
  private Scalar rotationAccRollingAverage = Quantity.of(0, SI.ANGULAR_ACCELERATION);

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
    Scalar expectedRoationAcceleration = estimateRotationAcceleration(expectedRotationVelocity);
    return getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation, //
        expectedRoationAcceleration);
  }

  private Scalar estimateRotationAcceleration(Scalar rotation) {
    if (lastRotation == null)
      lastRotation = rotation;
    double timeSinceLastStep = intervalClock.seconds();
    if (timeSinceLastStep >= MIN_DT) {
      Scalar instantRotChange = rotation.subtract(lastRotation).divide(Quantity.of(timeSinceLastStep, SI.SECOND));
      Scalar newPart = instantRotChange.multiply(ROLLING_AVERAGE_FACTOR);
      Scalar oldPart = rotationAccRollingAverage.multiply(RealScalar.ONE.subtract(ROLLING_AVERAGE_FACTOR));
      rotationAccRollingAverage = newPart.add(oldPart);
    }
    lastRotation = rotation;
    return rotationAccRollingAverage;
  }

  private Scalar getPredictiveComponent(Scalar expectedRotationAcceleration) {
    Scalar rotationalAcceleration = expectedRotationAcceleration;
    return rotationalAcceleration.multiply(torqueVectoringConfig.staticPrediction);
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
}
