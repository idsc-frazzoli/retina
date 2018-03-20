//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class InputSubModule implements DavisDvsListener {
  DavisSurfaceOfActiveEvents surface1 = new DavisSurfaceOfActiveEvents();
  private double backgroundActivityFilterTime = 0.2;
  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    surface1.updateSurface(davisDvsEvent);
    if(surface1.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime)) {
//      pass the filtered event stream to some other module
//      TODO: send the filtered stream to the GUI for visual inspection
    }
    
    
  }
}
