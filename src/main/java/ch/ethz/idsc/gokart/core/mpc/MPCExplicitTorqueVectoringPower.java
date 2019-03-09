// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedPredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class MPCExplicitTorqueVectoringPower extends MPCPower {
  private static final Scalar NOACCELERATION = Quantity.of(0, SI.ACCELERATION);
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
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
    Scalar braking = Max.of(NOACCELERATION, cnsStep.gokartControl.getaB().negate());
    Scalar leftPower = powerLookupTable.getNeededCurrent(//
        cnsStep.gokartControl.getuL().subtract(braking),//
        cnsStep.gokartState.getUx());
    Scalar rightPower = powerLookupTable.getNeededCurrent(//
        cnsStep.gokartControl.getuR().subtract(braking),//
        cnsStep.gokartState.getUx());
    return Optional.of(Tensors.of(leftPower,rightPower));
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
