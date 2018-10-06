// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

public class SEyeSlamLidarModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public SEyeSlamLidarModule() {
    super(SlamAlgoConfig.lidarReactiveMode, "sEye");
  }
}
