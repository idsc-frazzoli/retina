// code by mg
package ch.ethz.idsc.retina.app.slam.online;

import ch.ethz.idsc.retina.app.slam.SlamAlgoConfig;
import ch.ethz.idsc.retina.app.slam.config.EventCamera;

public class SEyeSlamVisualModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public SEyeSlamVisualModule() {
    super(EventCamera.SEYE, SlamAlgoConfig.standardReactiveMode);
  }
}
