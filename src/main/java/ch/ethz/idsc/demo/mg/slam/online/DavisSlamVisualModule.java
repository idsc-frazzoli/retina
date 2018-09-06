// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

public class DavisSlamVisualModule extends DavisSlamBaseModule {
  protected DavisSlamVisualModule() {
    super(SlamAlgoConfig.standardReactiveMode);
  }

  public static void standalone() throws Exception {
    DavisSlamVisualModule davisSlamVisualModule = new DavisSlamVisualModule();
    davisSlamVisualModule.launch();
  }
}
