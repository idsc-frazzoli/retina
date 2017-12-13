// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;

import ch.ethz.idsc.retina.dev.zhkart.joy.RimoTorqueJoystickModule;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PI controller of torque control */
public class RimoConfig implements Serializable {
  public static final RimoConfig GLOBAL = AppResources.load(new RimoConfig());

  private RimoConfig() {
  }

  /***************************************************/
  /** parameters for {@link RimoRateController}
   * rateLimit, Kp, Ki */
  public Scalar rateLimit = Quantity.of(20, "rad*s^-1");
  public Scalar Kp = Quantity.of(20, "ARMS*rad^-1*s"); // 40
  public Scalar Ki = Quantity.of(20, "ARMS*rad^-1"); // 15
  /** the physical maximum torque limit is 2316[ARMS]
   * the torque limit is used in {@link RimoTorqueJoystickModule} */
  public Scalar torqueLimit = Quantity.of(2000, "ARMS");
  /** threshold for angular speed beyond which brake will activate
   * by itself if autonomy determines safety issue */
  public Scalar deadManLimit = Quantity.of(4.0, "rad*s^-1");
  @Deprecated // constant was used for tuning the PI controller in the workshop
  public Scalar testPulseLo = Quantity.of(0, "rad*s^-1");
  @Deprecated // constant was used for tuning the PI controller in the workshop
  public Scalar testPulseHi = Quantity.of(20, "rad*s^-1");

  /***************************************************/
  /** @return clip interval for permitted torque */
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }
}
