// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DavisApsEvent;

/** listener to events that make up the grayscale image provided by the davis camera */
@FunctionalInterface
public interface DavisApsListener {
  /** @param davisApsEvent */
  void davisAps(DavisApsEvent davisApsEvent);
}
