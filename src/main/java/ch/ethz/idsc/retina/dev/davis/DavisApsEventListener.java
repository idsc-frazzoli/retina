// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis._240c.DavisApsEvent;

/** listener to events that make up the grayscale image
 * provided by the davis camera */
public interface DavisApsEventListener extends DavisEventListener {
  void aps(DavisApsEvent davisApsEvent);
}
