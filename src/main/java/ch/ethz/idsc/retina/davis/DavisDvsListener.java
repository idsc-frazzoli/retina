// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** listener receives davis dvs events */
@FunctionalInterface
public interface DavisDvsListener {
  /** @param davisDvsEvent */
  void davisDvs(DavisDvsEvent davisDvsEvent);
}
