package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.joy.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.joy.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerMapping;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Tan;

public class MPCTorqueVectoringPower implements MPCPower {
  ControlAndPredictionSteps cns;
  ImprovedNormalizedTorqueVectoring torqueVectoring;
  MPCStateEstimationProvider mpcStateProvider;
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  MPCSteering mpcSteering;
  int inext = 0;

  public MPCTorqueVectoringPower(MPCSteering mpcSteering) {
    this.mpcSteering = mpcSteering;
    torqueVectoring = new ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  }

  @Override
  public Tensor getPower(Scalar time) {
    // find at which stage we are
    while (//
    Scalars.lessThan(//
        time, //
        cns.steps[inext].state.getTime())) {
      inext++;
    }
    if (inext > 0) {
      // steps are not in the future
      // TODO: this leads to multiple executions. Fix that.
      Scalar theta = steerMapping.getAngleFromSCE(mpcSteering.getSteering(time)); // steering angle of imaginary front wheel
      Scalar expectedRotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
      Scalar currentSlip = mpcStateProvider.getState().getdotPsi().subtract(expectedRotationPerMeterDriven);
      Scalar wantedAcceleration = cns.steps[inext - 1].control.getaB();// when used in
      return torqueVectoring.getMotorCurrentsFromAcceleration(//
          expectedRotationPerMeterDriven, //
          mpcStateProvider.getState().getUx(), //
          currentSlip, //
          wantedAcceleration, //
          mpcStateProvider.getState().getdotPsi());
    } else {
      return null;
    }
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
    inext = 0;
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
