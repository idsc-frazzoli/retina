// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public final class MPCRimoProvider implements PutProvider<RimoPutEvent> {
  private final Timing timing;
  private final MPCPower mpcPower;

  public MPCRimoProvider(Timing timing, MPCPower mpcPower) {
    this.timing = timing;
    this.mpcPower = mpcPower;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    Scalar time = Quantity.of(timing.seconds(), SI.SECOND);
    Tensor currents = mpcPower.getPower(time);
    if (Objects.nonNull(currents))
      return Optional.of(RimoPutHelper.operationTorque( //
          (short) -Magnitude.ARMS.toFloat(currents.Get(0)), // sign left invert
          (short) +Magnitude.ARMS.toFloat(currents.Get(1)) // sign right id
      ));
    return Optional.empty();
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
