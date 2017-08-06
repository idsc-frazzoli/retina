// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis._240c.ApsDavisEvent;

public interface ApsDavisEventListener extends DavisEventListener {
  void aps(ApsDavisEvent apsDavisEvent);
}
