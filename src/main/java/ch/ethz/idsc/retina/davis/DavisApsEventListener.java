// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DavisApsEvent;

public interface DavisApsEventListener extends DavisEventListener {
  void aps(DavisApsEvent davisApsEvent);
}
