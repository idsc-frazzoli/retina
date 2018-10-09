// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.config.EventCamera;

public class DavisSlamOdometryModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamOdometryModule() {
    super(EventCamera.DAVIS, SlamAlgoConfig.odometryReactiveMode);
  }
}
