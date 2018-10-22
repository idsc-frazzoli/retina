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

public class MPCTorqueVectroringPower implements MPCPower {
  ControlAndPredictionSteps cns;
  ImprovedNormalizedTorqueVectoring torqueVectoring;
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  MPCSteering mpcSteering;
  GokartState currentState;
  int inext = 0;

  public MPCTorqueVectroringPower(MPCSteering mpcSteering) {
    this.mpcSteering = mpcSteering;
    torqueVectoring = new ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  }

  @Override
  public void Update(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
    inext = 0;
  }

  @Override
  public Tensor getSteering(Scalar time) {
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
      Scalar currentSlip = currentState.getdotPsi().subtract(expectedRotationPerMeterDriven);
      Scalar wantedAcceleration = cns.steps[inext - 1].control.getaB();// when used in
      return torqueVectoring.getMotorCurrentsFromAcceleration(//
          expectedRotationPerMeterDriven, //
          currentState.getUx(), currentSlip, wantedAcceleration, currentState.getdotPsi());
    } else {
      return null;
    }
  }

  @Override
  public void getState(GokartState state) {
    currentState = state;
  }
}
