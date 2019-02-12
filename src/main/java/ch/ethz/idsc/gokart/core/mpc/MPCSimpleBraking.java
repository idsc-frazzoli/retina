// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.brake.StaticBrakeFunction;
import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class MPCSimpleBraking extends MPCBraking {
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private final MPCOptimizationConfig config = MPCOptimizationConfig.GLOBAL;

  @Override // from MPCBraking
  Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(config.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return RealScalar.ZERO;
    Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.gokartState.getUx());
    Scalar min = minmax.Get(0);
    Scalar braking = Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.gokartControl.getaB().negate().add(min));
    // System.out.println(braking);
    return StaticBrakeFunction.INSTANCE.getRelativeBrakeActuation(braking);
  }

  @Override
  public void setStateEstimationProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
    // ---
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
