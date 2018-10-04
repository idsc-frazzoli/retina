// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

public class DavisSlamLidarModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamLidarModule() {
    super(SlamAlgoConfig.lidarReactiveMode, "davis");
  }
}
