// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DvsDavisEvent;

// TODO document
public interface DvsDavisEventListener extends DavisEventListener {
  void dvs(DvsDavisEvent dvsDavisEvent);
}
