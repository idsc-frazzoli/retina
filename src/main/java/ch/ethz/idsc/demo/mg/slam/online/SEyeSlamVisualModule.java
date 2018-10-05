// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

public class SEyeSlamVisualModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  SEyeSlamVisualModule() {
    super(SlamAlgoConfig.standardReactiveMode, "sEye");
  }
}
