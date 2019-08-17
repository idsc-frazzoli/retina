// code by mg
package ch.ethz.idsc.retina.app.slam;

import ch.ethz.idsc.retina.app.slam.core.SlamAlgoConfiguration;

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
