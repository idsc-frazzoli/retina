// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.slam.algo.SlamAlgoConfiguration;

/** the difference between the different modes is described in {@link SlamAlgoConfiguration} in package
 * demo.mg.slam.algo */
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
