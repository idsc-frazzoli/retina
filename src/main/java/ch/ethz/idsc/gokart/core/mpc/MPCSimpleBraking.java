// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.brake.BrakingFunction;
import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

public class MPCSimpleBraking extends MPCBraking {
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private final MPCOptimizationConfig config = MPCOptimizationConfig.GLOBAL;

  @Override
  public Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(config.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return RealScalar.ZERO;
    Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    Scalar min = minmax.Get(0);
    Scalar braking = Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.control.getaB().negate().add(min));
    // System.out.println(braking);
    return BrakingFunction.getRelativeBrakeActuation(braking);
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcStateEstimationProvider) {
    // ---
  }
}
