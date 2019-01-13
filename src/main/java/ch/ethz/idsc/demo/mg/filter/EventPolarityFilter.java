// code by mg
package ch.ethz.idsc.demo.mg.filter;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

public enum EventPolarityFilter implements DavisDvsEventFilter {
  BOTH() {
    @Override // from DavisDvsEventFilter
    public boolean filter(DavisDvsEvent davisDvsEvent) {
      return true;
    }
  }, //
  DARK_TO_BRIGHT() {
    @Override // from DavisDvsEventFilter
    public boolean filter(DavisDvsEvent davisDvsEvent) {
      return davisDvsEvent.darkToBright();
    }
  }, //
  BRIGHT_TO_DARK() {
    @Override // from DavisDvsEventFilter
    public boolean filter(DavisDvsEvent davisDvsEvent) {
      return davisDvsEvent.brightToDark();
    }
  }, //
  ;
}
