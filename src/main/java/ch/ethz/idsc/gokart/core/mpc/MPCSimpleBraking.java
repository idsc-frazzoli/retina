// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

public class MPCSimpleBraking extends MPCBraking {
  MPCStateEstimationProvider mpcStateProvider;
  final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  MPCOptimizationConfig config = MPCOptimizationConfig.GLOBAL;
  int inext = 0;

  @Override
  public Scalar getBraking(Scalar time) {
    Scalar controlTime = time.add(config.brakingAntiLag);
    ControlAndPredictionStep cnsStep = getStep(controlTime);
    if (Objects.isNull(cnsStep))
      return Quantity.of(0, SI.ONE);
    Tensor minmax = powerLookupTable.getMinMaxAcceleration(cnsStep.state.getUx());
    Scalar min = minmax.Get(0);
    Scalar braking = (Scalar) Max.of(Quantity.of(0, SI.ACCELERATION), cnsStep.control.getaB().negate().add(min));
    System.out.println(braking);
    Scalar brakePos = BrakingFunction.getNeededBrakeActuation(braking);
    if (brakePos == null)
      return RealScalar.ZERO;
    return BrakingFunction.getRelativePosition(brakePos);
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
