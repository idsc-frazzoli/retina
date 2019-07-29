// code by mh
package ch.ethz.idsc.gokart.calib.power;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PredictiveMotorCurrents implements MotorCurrentsInterface {
  private static final double MIN_DT = 0.000001;
  private static final Scalar ROLLING_AVERAGE_VALUE = Quantity.of(0.0, SI.ANGULAR_ACCELERATION);
  // ---
  private final TorqueVectoringConfig torqueVectoringConfig;
  private final IntervalClock intervalClock = new IntervalClock();
  private final GeodesicIIR1 geodesicIIR1;
  private Scalar wantedRotationRate_last = null;
  private Scalar rotationAcc_fallback = ROLLING_AVERAGE_VALUE;

  public PredictiveMotorCurrents(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
    geodesicIIR1 = new GeodesicIIR1( //
        RnGeodesic.INSTANCE, //
        torqueVectoringConfig.rollingAverageRatio /* ROLLING_AVERAGE_VALUE */ );
  }

  @Override // from MotorCurrentsInterface
  public final Tensor fromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration) {
    Scalar wantedRotationRate = angularSlip.wantedRotationRate(); // s^-1
    Scalar expectedRotationAcceleration = estimateRotationAcceleration(wantedRotationRate, intervalClock.seconds());
    Scalar predictiveComponent = torqueVectoringConfig.getPredictiveComponent(expectedRotationAcceleration);
    // ---
    Scalar wantedZTorque = torqueVectoringConfig.wantedZTorque( //
        torqueVectoringConfig.getDynamicAndStatic(angularSlip).add(predictiveComponent), // One
        angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return StaticHelper.getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, angularSlip.tangentSpeed());
  }

  /** @param wantedRotationRate [s^-1]
   * @param timeSinceLastStep with interpretation in seconds
   * @return estimation of rotational acceleration with unit [s^-2] */
  /* package */ Scalar estimateRotationAcceleration(Scalar wantedRotationRate, double timeSinceLastStep) {
    if (Objects.isNull(wantedRotationRate_last))
      wantedRotationRate_last = wantedRotationRate;
    if (timeSinceLastStep >= MIN_DT) {
      Scalar instantRotChange = wantedRotationRate.subtract(wantedRotationRate_last).divide(Quantity.of(timeSinceLastStep, SI.SECOND));
      rotationAcc_fallback = geodesicIIR1.apply(instantRotChange).Get();
    }
    wantedRotationRate_last = wantedRotationRate;
    return rotationAcc_fallback;
  }
}
