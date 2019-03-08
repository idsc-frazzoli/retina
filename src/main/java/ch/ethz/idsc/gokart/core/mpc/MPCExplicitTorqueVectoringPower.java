// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedPredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class MPCExplicitTorqueVectoringPower extends MPCPower {
  private static final Scalar NOACCELERATION = Quantity.of(0, SI.ACCELERATION);
  // private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final ImprovedNormalizedTorqueVectoring torqueVectoring = //
      new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL);
  // ---
  private final MPCStateEstimationProvider mpcStateEstimationProvider;

  public MPCExplicitTorqueVectoringPower(MPCStateEstimationProvider mpcStateEstimationProvider) {
    this.mpcStateEstimationProvider = Objects.requireNonNull(mpcStateEstimationProvider);
  }

  @Override
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    // Tensor steering = optional.get();
    // Scalar theta = steerMapping.getAngleFromSCE(steering.Get(0)); // steering angle of imaginary front wheel
    // Scalar expectedRotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(ChassisGeometry.GLOBAL.xAxleRtoF); // m^-1
    // Scalar tangentialSpeed = mpcStateEstimationProvider.getState().getUx();
    // Scalar wantedRotationRate = expectedRotationPerMeterDriven.multiply(tangentialSpeed); // unit s^-1
    // Scalar wantedAcceleration = cnsStep.gokartControl.getaB();// when used in
    // get midpoint of powered acceleration range
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar midpoint = (Scalar) Mean.of(minmax);
    // more tame version
    // FIXME MARC !!!
    return null;
  }

  @Override
  public void start() {
    // TODO MH document why empty
  }

  @Override
  public void stop() {
    // TODO MH document why empty
  }
}
