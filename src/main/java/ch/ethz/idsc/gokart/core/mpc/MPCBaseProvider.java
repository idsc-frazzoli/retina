// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.io.Timing;

public abstract class MPCBaseProvider<T> implements PutProvider<T> {
  final Timing timing;

  public MPCBaseProvider(Timing timing) {
    this.timing = timing;
  }

  @Override
  public final ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
