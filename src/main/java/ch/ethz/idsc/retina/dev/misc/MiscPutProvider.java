// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.util.Optional;

public interface MiscPutProvider {
  Optional<MiscPutEvent> pollMiscPut();
}
