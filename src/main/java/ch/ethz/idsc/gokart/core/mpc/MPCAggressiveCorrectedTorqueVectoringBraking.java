// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.brake.BrakingFunction;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class MPCAggressiveCorrectedTorqueVectoringBraking extends MPCBraking {
  private static final Scalar NO_ACCELERATION = Quantity.of(0, SI.ACCELERATION);
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;
  private final MPCActiveCompensationLearning activeCompensationLearning = MPCActiveCompensationLearning.getInstance();

  @Override
  public Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return RealScalar.ZERO;
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar min = (Scalar) Mean.of(minmax);
    // Scalar braking = Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.control.getaB().negate().add(min));
    Scalar braking = Max.of(NO_ACCELERATION, cnsStep.control.getaB().negate());
    // System.out.println(braking);
    return BrakingFunction.getInstance().getRelativeBrakeActuation(braking);
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
    // ---
  }
}
