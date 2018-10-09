// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.config.EventCamera;

public class DavisSlamLidarModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamLidarModule() {
    super(EventCamera.DAVIS, SlamAlgoConfig.lidarReactiveMode);
  }
}
