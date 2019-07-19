// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.power.MotorCurrentsInterface;
import ch.ethz.idsc.gokart.calib.power.PredictiveMotorCurrents;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class MPCTorqueVectoringPower extends MPCPower {
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final MotorCurrentsInterface motorCurrentsInterface = //
      new PredictiveMotorCurrents(TorqueVectoringConfig.GLOBAL);
  private final MPCSteering mpcSteering;
  // ---
  private final MPCStateEstimationProvider mpcStateEstimationProvider;

  public MPCTorqueVectoringPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    this.mpcStateEstimationProvider = Objects.requireNonNull(mpcStateEstimationProvider);
    this.mpcSteering = mpcSteering;
  }

  @Override // from MPCPower
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    Optional<Tensor> optional = mpcSteering.getSteering(time);
    if (!optional.isPresent())
      return Optional.empty();
    GokartState gokartState = mpcStateEstimationProvider.getState();
    Scalar ratio = steerMapping.getRatioFromSCE(optional.get().Get(0)); // steering angle of imaginary front wheel
    Scalar tangentialSpeed = gokartState.getUx();
    // compute (negative) angular slip
    Scalar gyroZ = gokartState.getGyroZ(); // unit s^-1
    Scalar wantedAcceleration = cnsStep.gokartControl().getaB();
    return Optional.of(motorCurrentsInterface.fromAcceleration( //
        new AngularSlip(tangentialSpeed, ratio, gyroZ), wantedAcceleration));
  }
}
