// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.mpc.PowerLookupTable;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Times;

public class ImprovedNormalizedPredictiveTorqueVectoring extends ImprovedNormalizedTorqueVectoring {
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private Scalar lastRotationPerMeterDriven = null;
  private Scalar rotationAccRollingAverage = Quantity.of(0, SI.ANGULAR_ACCELERATION.add(SI.METER.negate()));
  private Scalar rollinAverageFactor = RealScalar.of(0.5); // good data expected
  private Stopwatch lastMeasure = Stopwatch.started();

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

  public Scalar estimateRotationPerMeterChange(Scalar rotationPerMeter) {
    if (lastRotationPerMeterDriven == null)
      lastRotationPerMeterDriven = rotationPerMeter;
    double timeSinceLastStep = lastMeasure.display_seconds();
    lastMeasure = Stopwatch.started();
    if (timeSinceLastStep >= 0.000001) {
      Scalar instantRotPerMetChange = rotationPerMeter.subtract(lastRotationPerMeterDriven).divide(Quantity.of(timeSinceLastStep, SI.SECOND));
      rotationAccRollingAverage = rotationAccRollingAverage.multiply(RealScalar.ONE.subtract(rollinAverageFactor))//
          .add(instantRotPerMetChange.multiply(rollinAverageFactor));
    }
    return rotationAccRollingAverage;
  }

  @Override
  public Tensor getMotorCurrentsFromAcceleration(//
      Scalar expectedRotationPerMeterDriven, //
      Scalar meanTangentSpeed, //
      Scalar angularSlip, //
      Scalar wantedAcceleration, Scalar realRotation) {
    Scalar expectedRotationPerMeterDrivenChange//
        = estimateRotationPerMeterChange(expectedRotationPerMeterDriven);
    Scalar expectedRotationAcceleration = expectedRotationPerMeterDrivenChange*meanTangentSpeed;
    return getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation, //
        expectedRotationAcceleration);
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
