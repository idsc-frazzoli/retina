// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;

class SEyeSlamOdometryModule extends DvsSlamBaseModule {
  /** public constructor for invocation in GUI */
  SEyeSlamOdometryModule() {
    super(SlamAlgoConfig.odometryReactiveMode, "sEye");
  }
}
