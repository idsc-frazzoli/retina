// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

// TODO in online use, the window should not be closable by pressing (X)
// ... only if stop() is called
public class DavisSlamVisualModule extends DavisSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamVisualModule() {
    super(SlamAlgoConfig.standardReactiveMode);
  }
}
