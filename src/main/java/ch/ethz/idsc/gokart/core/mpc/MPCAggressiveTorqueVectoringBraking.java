// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.brake.StaticBrakeFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class MPCAggressiveTorqueVectoringBraking extends MPCBraking {
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;

  @Override // from MPCBraking
  Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return RealScalar.ZERO;
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar min = (Scalar) Mean.of(minmax);
    // Scalar braking = Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.control.getaB().negate().add(min));
    Scalar braking = Ramp.FUNCTION.apply(cnsStep.gokartControl().getaB().negate());
    // System.out.println(braking);
    return StaticBrakeFunction.INSTANCE.getRelativeBrakeActuation(braking);
  }

  @Override
  public void setStateEstimationProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
    // TODO MH is this function needed at all ?
  }

  @Override
  public void start() {
    // TODO MH document that empty implementation is desired
  }

  @Override
  public void stop() {
    // TODO MH document that empty implementation is desired
  }
}
