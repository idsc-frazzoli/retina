// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.brake.StaticBrakeFunction;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class MPCAggressiveTorqueVectoringBraking extends MPCBraking {
  private static final Scalar NOACCELERATION = Quantity.of(0, SI.ACCELERATION);
  private final MPCOptimizationConfig config = MPCOptimizationConfig.GLOBAL;

  @Override
  public Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(config.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return RealScalar.ZERO;
    // Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    // Scalar min = (Scalar) Mean.of(minmax);
    // Scalar braking = Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.control.getaB().negate().add(min));
    Scalar braking = Max.of(NOACCELERATION, cnsStep.gokartControl.getaB().negate());
    // System.out.println(braking);
    return StaticBrakeFunction.INSTANCE.getRelativeBrakeActuation(braking);
  }

  @Override
  public void setStateEstimationProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
    // ---
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }
}
