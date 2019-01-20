// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.sys.AppResources;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class HighPowerSteerConfig extends SteerConfig {
  public static final HighPowerSteerConfig GLOBAL = AppResources.load(new HighPowerSteerConfig());
}
