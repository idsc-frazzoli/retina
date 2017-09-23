// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Optional;

public interface RimoPutProvider {
  Optional<RimoPutEvent> pollRimoPut();
}
