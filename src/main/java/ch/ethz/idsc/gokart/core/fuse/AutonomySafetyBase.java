// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.function.Supplier;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.data.DataEvent;

/* package */ abstract class AutonomySafetyBase<T extends DataEvent> implements PutProvider<T> {
  final Supplier<Boolean> supplier;

  /** @param supplier that returns true if autonomous driving mode is permitted */
  public AutonomySafetyBase(Supplier<Boolean> supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.SAFETY;
  }
}
