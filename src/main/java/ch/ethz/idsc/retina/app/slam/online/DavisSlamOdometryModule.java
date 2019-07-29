// code by mg
package ch.ethz.idsc.retina.app.slam.online;

import ch.ethz.idsc.retina.app.slam.SlamAlgoConfig;
import ch.ethz.idsc.retina.app.slam.config.EventCamera;

public class DavisSlamOdometryModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamOdometryModule() {
    super(EventCamera.DAVIS, SlamAlgoConfig.odometryReactiveMode);
  }
}
