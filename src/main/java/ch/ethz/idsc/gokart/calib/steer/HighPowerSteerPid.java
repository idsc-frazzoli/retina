// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerPid;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for aggressive PID controller of steering */
public class HighPowerSteerPid extends SteerPid {
  public static final HighPowerSteerPid GLOBAL = AppResources.load(new HighPowerSteerPid());

  /***************************************************/
  public HighPowerSteerPid() {
    Ki = Quantity.of(5.7, "SCE^-1*SCT*s^-1");
    Kp = Quantity.of(7.2, "SCE^-1*SCT");
    Kd = Quantity.of(0.82, "SCE^-1*SCT*s");
    torqueLimit = Quantity.of(3, "SCT");
  }
}
