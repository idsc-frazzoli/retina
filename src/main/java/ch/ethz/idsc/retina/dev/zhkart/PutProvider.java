// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Optional;

public interface PutProvider<T> {
  /** @return */
  ProviderRank getProviderRank();

  /** @return */
  Optional<T> getPutEvent();
}
