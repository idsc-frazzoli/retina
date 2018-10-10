// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.config.EventCamera;

// TODO in online use, the window should not be closable by pressing (X)
// ... only if stop() is called
public class DavisSlamVisualModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamVisualModule() {
    super(EventCamera.DAVIS, SlamAlgoConfig.standardReactiveMode);
  }
}
