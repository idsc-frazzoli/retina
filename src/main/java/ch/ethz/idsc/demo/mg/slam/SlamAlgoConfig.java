// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.slam.core.SlamAlgoConfiguration;

/** the difference between the different modes is described in {@link SlamAlgoConfiguration} in package
 * demo.mg.slam.core */
public enum SlamAlgoConfig {
  standardMode, //
  standardReactiveMode, //
  lidarMode, //
  lidarReactiveMode, //
  odometryMode, //
  odometryReactiveMode, //
  localizationMode, //
  ;
}
