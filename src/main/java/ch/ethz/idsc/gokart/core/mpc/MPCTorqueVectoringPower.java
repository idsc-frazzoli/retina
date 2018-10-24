// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.joy.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.joy.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerMapping;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Tan;

public class MPCTorqueVectoringPower extends MPCPower {
  ImprovedNormalizedTorqueVectoring torqueVectoring;
  MPCStateEstimationProvider mpcStateProvider;
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  MPCSteering mpcSteering;

  public MPCTorqueVectoringPower(MPCSteering mpcSteering) {
    this.mpcSteering = mpcSteering;
    torqueVectoring = new ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  }

  @Override
  public Tensor getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (cnsStep == null) {
      return Tensors.of(//
          Quantity.of(0, NonSI.ARMS), //
          Quantity.of(0, NonSI.ARMS));
    } else {
      Scalar theta = steerMapping.getAngleFromSCE(mpcSteering.getSteering(time)); // steering angle of imaginary front wheel
      Scalar expectedRotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
      Scalar currentSlip = mpcStateProvider.getState().getdotPsi().subtract(expectedRotationPerMeterDriven);
      Scalar wantedAcceleration = cnsStep.control.getaB();// when used in
      return torqueVectoring.getMotorCurrentsFromAcceleration(//
          expectedRotationPerMeterDriven, //
          mpcStateProvider.getState().getUx(), //
          currentSlip, //
          wantedAcceleration, //
          mpcStateProvider.getState().getdotPsi());
    }
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
