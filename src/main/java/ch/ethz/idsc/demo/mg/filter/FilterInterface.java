// code by jph, mg
package ch.ethz.idsc.demo.mg.filter;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public interface FilterInterface {
  /** @return true if event passes filter */
  boolean filter(DavisDvsEvent davisDvsEvent);

  /** @return percentage of events that are filtered out */
  double getFilteredPercentage();
}
