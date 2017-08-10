// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.ApsDavisEvent;

public interface ApsDavisEventListener extends DavisEventListener {
  void aps(ApsDavisEvent apsDavisEvent);
}
