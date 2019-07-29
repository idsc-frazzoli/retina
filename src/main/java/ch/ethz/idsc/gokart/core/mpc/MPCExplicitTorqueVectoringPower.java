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

  @Override // from MPCPower
  Optional<Tensor> getPower(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Optional.empty();
    GokartControl gokartControl = cnsStep.gokartControl();
    GokartState gokartState = cnsStep.gokartState();
    Scalar braking = Ramp.FUNCTION.apply(gokartControl.getaB().negate());
    Scalar velocity = gokartState.getUx();
    return Optional.of(Tensors.of( //
        powerLookupTable.getNeededCurrent(gokartControl.getuL().add(braking), velocity), //
        powerLookupTable.getNeededCurrent(gokartControl.getuR().add(braking), velocity)));
  }
}
