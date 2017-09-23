// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.util.Optional;

public interface SteerPutProvider {
  Optional<SteerPutEvent> pollSteerPut();
}
