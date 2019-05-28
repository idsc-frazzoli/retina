// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import java.util.Objects;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ImprovedNormalizedPredictiveTorqueVectoring extends ImprovedNormalizedTorqueVectoring {
  /** min_dt with interpretation in seconds */
  private static final double MIN_DT = 0.000001;
  private static final Scalar ROLLING_AVERAGE_VALUE = Quantity.of(0.0, SI.ANGULAR_ACCELERATION);
  // ---
  private final IntervalClock intervalClock = new IntervalClock();
  private final GeodesicIIR1Filter geodesicIIR1Filter;
  private Scalar wantedRotationRate_last = null;
  private Scalar rotationAcc_fallback = ROLLING_AVERAGE_VALUE;

  public ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
    geodesicIIR1Filter = new GeodesicIIR1Filter( //
        RnGeodesic.INSTANCE, //
        torqueVectoringConfig.rollingAverageRatio /* ROLLING_AVERAGE_VALUE */ );
  }

  @Override // from ImprovedNormalizedTorqueVectoring
  public final Tensor getMotorCurrentsFromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration) {
    Scalar wantedRotationRate = angularSlip.wantedRotationRate(); // s^-1
    Scalar expectedRotationAcceleration = estimateRotationAcceleration(wantedRotationRate, intervalClock.seconds());
    return getMotorCurrentsFromAcceleration(//
        angularSlip, //
        wantedAcceleration, //
        expectedRotationAcceleration);
  }

  /** @param wantedRotationRate [s^-1]
   * @param timeSinceLastStep with interpretation in seconds
   * @return estimation of rotational acceleration with unit [s^-2] */
  /* package */ Scalar estimateRotationAcceleration(Scalar wantedRotationRate, double timeSinceLastStep) {
    if (Objects.isNull(wantedRotationRate_last))
      wantedRotationRate_last = wantedRotationRate;
    if (timeSinceLastStep >= MIN_DT) {
      Scalar instantRotChange = wantedRotationRate.subtract(wantedRotationRate_last).divide(Quantity.of(timeSinceLastStep, SI.SECOND));
      rotationAcc_fallback = geodesicIIR1Filter.apply(instantRotChange).Get();
    }
    wantedRotationRate_last = wantedRotationRate;
    return rotationAcc_fallback;
  }

  private Tensor getMotorCurrentsFromAcceleration( //
      AngularSlip angularSlip, Scalar wantedAcceleration, Scalar expectedRotationAcceleration) {
    Scalar dynamicComponent = getDynamicComponent(angularSlip.angularSlip());
    Scalar staticComponent = getStaticComponent(angularSlip.rotationPerMeterDriven(), angularSlip.tangentSpeed());
    Scalar predictiveComponent = getPredictiveComponent(expectedRotationAcceleration);
    // ---
    Scalar wantedZTorque = wantedZTorque( //
        dynamicComponent.add(staticComponent).add(predictiveComponent), // One
        angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, angularSlip.tangentSpeed());
  }

  private Scalar getPredictiveComponent(Scalar expectedRotationAcceleration) {
    return expectedRotationAcceleration.multiply(torqueVectoringConfig.staticPrediction);
  }
}
