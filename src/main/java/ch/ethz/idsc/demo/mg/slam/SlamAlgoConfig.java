// code by mg
package ch.ethz.idsc.demo.mg.slam;

/** the difference between the different modes is described in {@link SlamAlgoConfig} in package
 * demo.mg.slam.algo */
// TODO MG rename either one of the enums that now have the same name
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
