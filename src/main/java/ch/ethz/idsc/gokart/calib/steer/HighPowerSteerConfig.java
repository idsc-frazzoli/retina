// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.sys.AppResources;

/** parameters for aggressive PID controller of steering */
public class HighPowerSteerConfig extends SteerConfig {
  // TODO JPH contains calibration even though only pid constants are needed -> refactor
  public static final HighPowerSteerConfig GLOBAL = AppResources.load(new HighPowerSteerConfig());
}
