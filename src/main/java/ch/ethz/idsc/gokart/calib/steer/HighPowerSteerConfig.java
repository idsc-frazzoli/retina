// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for aggressive PID controller of steering */
public class HighPowerSteerConfig extends SteerConfig {
  // TODO JPH contains calibration even though only pid constants are needed -> refactor
  public static final HighPowerSteerConfig GLOBAL = AppResources.load(new HighPowerSteerConfig());

  public HighPowerSteerConfig() {
    Ki = Quantity.of(5.7, "SCE^-1*SCT*s^-1");
    Kp = Quantity.of(7.2, "SCE^-1*SCT");
    Kd = Quantity.of(0.82, "SCE^-1*SCT*s");
    torqueLimit = Quantity.of(3, "SCT");
  }
}
