// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

public class SEyeSlamOdometryModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  public SEyeSlamOdometryModule() {
    super(SlamAlgoConfig.odometryReactiveMode, "sEye");
  }
}
