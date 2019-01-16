// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.mpc.PowerLookupTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ImprovedNormalizedPredictiveTorqueVectoring extends ImprovedNormalizedTorqueVectoring {
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private Scalar lastRotation = null;
  private Scalar rotationAccRollingAverage = Quantity.of(0, SI.ANGULAR_ACCELERATION);
  private Scalar rollinAverageFactor = RealScalar.of(0.5); // good data expected
  private Timing lastMeasure = Timing.started();

  public ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override
  public Tensor powers(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar power, Scalar realRotation) {
    // wrapper for torque vectoring method
    Scalar wantedAcceleration = powerLookupTable.getNormalizedAccelerationTorqueCentered(power, meanTangentSpeed);
    Tensor motorCurrents = getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation);
    return motorCurrents.divide(ManualConfig.GLOBAL.torqueLimit);
  }

  public Scalar estimateRotationAcceleration(Scalar rotation) {
    if (lastRotation == null)
      lastRotation = rotation;
    double timeSinceLastStep = lastMeasure.seconds();
    lastMeasure = Timing.started();
    if (timeSinceLastStep >= 0.000001) {
      Scalar instantRotChange = rotation.subtract(lastRotation).divide(Quantity.of(timeSinceLastStep, SI.SECOND));
      Scalar newPart = instantRotChange.multiply(rollinAverageFactor);
      Scalar oldPart = rotationAccRollingAverage.multiply(RealScalar.ONE.subtract(rollinAverageFactor));
      rotationAccRollingAverage = newPart.add(oldPart);
    }
    return rotationAccRollingAverage;
  }

  @Override
  public Tensor getMotorCurrentsFromAcceleration(//
      Scalar expectedRotationPerMeterDriven, //
      Scalar meanTangentSpeed, //
      Scalar angularSlip, //
      Scalar wantedAcceleration, Scalar realRotation) {
    Scalar expectedRotationVelocity = meanTangentSpeed.multiply(expectedRotationPerMeterDriven);
    Scalar expectedRoationAcceleration//
        = estimateRotationAcceleration(expectedRotationVelocity);
    return getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation, //
        expectedRoationAcceleration);
  }

  final Scalar getPredictiveComponent(Scalar expectedRotationAcceleration) {
    Scalar rotationalAcceleration = expectedRotationAcceleration;
    return rotationalAcceleration.multiply(torqueVectoringConfig.staticPrediction);
  }

  public Tensor getMotorCurrentsFromAcceleration(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar wantedAcceleration,
      Scalar realRotation, Scalar expectedRotationAcceleration) {
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
