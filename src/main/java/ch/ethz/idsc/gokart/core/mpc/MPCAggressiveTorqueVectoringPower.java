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
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Tan;

/* package */ class MPCAggressiveTorqueVectoringPower extends MPCPower {
  private static final Scalar NOACCELERATION = Quantity.of(0, SI.ACCELERATION);
  // private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final ImprovedNormalizedTorqueVectoring torqueVectoring = //
      new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  private final MPCSteering mpcSteering;
  // ---
  private final MPCStateEstimationProvider mpcStateEstimationProvider;

  public MPCAggressiveTorqueVectoringPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    this.mpcStateEstimationProvider = Objects.requireNonNull(mpcStateEstimationProvider);
    this.mpcSteering = mpcSteering;
  }

  @Override
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    Optional<Tensor> optional = mpcSteering.getSteering(time);
    if (!optional.isPresent())
      return Optional.empty();
    Tensor steering = optional.get();
    Scalar theta = steerMapping.getAngleFromSCE(steering.Get(0)); // steering angle of imaginary front wheel
    Scalar expectedRotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
    Scalar tangentialSpeed = mpcStateEstimationProvider.getState().getUx();
    // Scalar wantedRotationRate = expectedRotationPerMeterDriven.multiply(tangentialSpeed); // unit s^-1
    // compute (negative) angular slip
    Scalar gyroZ = mpcStateEstimationProvider.getState().getdotPsi(); // unit s^-1
    Scalar angularSlip = AngularSlip.of(theta, ChassisGeometry.GLOBAL.xAxleRtoF, tangentialSpeed, gyroZ);
    // wantedRotationRate.subtract(gyroZ);
    Scalar wantedAcceleration = cnsStep.gokartControl().getaB();// when used in
    // get midpoint of powered acceleration range
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar midpoint = (Scalar) Mean.of(minmax);
    // more tame version
    return Optional.of(torqueVectoring.getMotorCurrentsFromAcceleration(//
        expectedRotationPerMeterDriven, //
        tangentialSpeed, //
        angularSlip, //
        Max.of(NOACCELERATION, wantedAcceleration), //
        gyroZ));
  }
}
