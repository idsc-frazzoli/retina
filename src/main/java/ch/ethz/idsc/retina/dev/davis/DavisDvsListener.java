// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** listener receives davis dvs events */
public interface DavisDvsListener extends DavisEventListener {
  void davisDvs(DavisDvsEvent davisDvsEvent);
}
