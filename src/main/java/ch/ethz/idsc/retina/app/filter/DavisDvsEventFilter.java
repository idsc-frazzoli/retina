// code by mg, jph
package ch.ethz.idsc.retina.app.filter;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

public interface DavisDvsEventFilter {
  /** @return true if event passes filter */
  boolean filter(DavisDvsEvent davisDvsEvent);
}
