// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedPredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Tan;

/* package */ class MPCTorqueVectoringPower extends MPCPower {
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final ImprovedNormalizedTorqueVectoring torqueVectoring = //
      new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  private final MPCSteering mpcSteering;
  // ---
  private final MPCStateEstimationProvider mpcStateEstimationProvider;

  public MPCTorqueVectoringPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    this.mpcStateEstimationProvider = mpcStateEstimationProvider;
    this.mpcSteering = mpcSteering;
  }

  @Override // from MPCPower
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    if (Objects.isNull(mpcStateEstimationProvider)) {
      // return torqueless power
      return Optional.of(torqueVectoring.getMotorCurrentsFromAcceleration(//
          Quantity.of(0, SI.SECOND.negate()), //
          cnsStep.gokartState.getUx(), //
          Quantity.of(0, SI.SECOND.negate()), //
          cnsStep.gokartControl.getaB(), //
          Quantity.of(0, SI.SECOND.negate())));
    }
    Optional<Tensor> optional = mpcSteering.getSteering(time);
    if (!optional.isPresent())
      return Optional.empty();
    Scalar theta = steerMapping.getAngleFromSCE(optional.get().Get(0)); // steering angle of imaginary front wheel
    Scalar expectedRotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
    Scalar tangentialSpeed = mpcStateEstimationProvider.getState().getUx();
    Scalar wantedRotationRate = expectedRotationPerMeterDriven.multiply(tangentialSpeed); // unit s^-1
    // compute (negative) angular slip
    Scalar gyroZ = mpcStateEstimationProvider.getState().getdotPsi(); // unit s^-1
    Scalar angularSlip = wantedRotationRate.subtract(gyroZ);
    Scalar wantedAcceleration = cnsStep.gokartControl.getaB();// when used in
    return Optional.of(torqueVectoring.getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        tangentialSpeed, //
        angularSlip, //
        wantedAcceleration, //
        gyroZ));
  }

  // @Override // from MPCStateProviderClient
  // public void setStateEstimationProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
  // this.mpcStateEstimationProvider = mpcStateEstimationProvider;
  // }
  @Override
  public void start() {
    // TODO MH document that empty implementation is intended
  }

  @Override
  public void stop() {
    // TODO MH document that empty implementation is intended
  }
}
