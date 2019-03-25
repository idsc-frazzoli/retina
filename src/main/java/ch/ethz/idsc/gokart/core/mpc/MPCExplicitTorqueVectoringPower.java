// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class MPCExplicitTorqueVectoringPower extends MPCPower {
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();

  @Override
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    Scalar braking = Ramp.FUNCTION.apply(cnsStep.gokartControl().getaB().negate());
    Scalar leftPower = powerLookupTable.getNeededCurrent(//
        cnsStep.gokartControl().getuL().add(braking), //
        cnsStep.gokartState().getUx());
    Scalar rightPower = powerLookupTable.getNeededCurrent(//
        cnsStep.gokartControl().getuR().add(braking), //
        cnsStep.gokartState().getUx());
    return Optional.of(Tensors.of(leftPower, rightPower));
  }
}
