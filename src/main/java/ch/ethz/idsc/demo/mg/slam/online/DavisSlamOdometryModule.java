// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

public class DavisSlamOdometryModule extends DavisSlamBaseModule {
  /** public constructor for invocation in GUI */
  public DavisSlamOdometryModule() {
    super(SlamAlgoConfig.odometryReactiveMode);
  }
}
