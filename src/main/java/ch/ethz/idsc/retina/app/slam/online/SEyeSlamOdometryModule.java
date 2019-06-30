// code by mg
package ch.ethz.idsc.retina.app.slam.online;

import ch.ethz.idsc.retina.app.slam.SlamAlgoConfig;
import ch.ethz.idsc.retina.app.slam.config.EventCamera;

public class SEyeSlamOdometryModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public SEyeSlamOdometryModule() {
    super(EventCamera.SEYE, SlamAlgoConfig.odometryReactiveMode);
  }
}
