// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

public interface LinmotPutProvider {
  Optional<LinmotPutEvent> pollLinmotPut();
}
