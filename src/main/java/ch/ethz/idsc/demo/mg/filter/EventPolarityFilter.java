// code by mg
package ch.ethz.idsc.demo.mg.filter;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/* package */ class EventPolarityFilter implements DavisDvsEventFilter {
  private final EventPolarityConfig eventPolarityConfig = SlamCoreConfig.GLOBAL.eventPolarityConfig;

  @Override // from DavisDvsEventFilter
  public boolean filter(DavisDvsEvent davisDvsEvent) {
    switch (eventPolarityConfig) {
    case both:
      return true;
    case darkToBright:
      return davisDvsEvent.darkToBright();
    case brightToDark:
      return davisDvsEvent.brightToDark();
    default:
      throw new RuntimeException();
    }
  }
}
