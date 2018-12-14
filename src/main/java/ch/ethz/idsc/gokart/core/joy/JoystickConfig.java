// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.dev.HybridControlProvider;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PI controller of torque control */
public class JoystickConfig {
  public static final JoystickConfig GLOBAL = AppResources.load(new JoystickConfig());
  /***************************************************/
  /** the physical maximum torque limit is 2316[ARMS]
   * the torque limit is used in {@link RimoTorqueJoystickModule} */
  public Scalar torqueLimit = Quantity.of(2315, NonSI.ARMS);

  /***************************************************/
  /** @return clip interval for permitted torque */
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }

  /** .
   * ante 20181211: the generic gamepad joystick was used
   * post 20181211: throttle pedal and boost button
   * 
   * @return manual control as configured on the gokart */
  public ManualControlProvider createProvider() {
    return new HybridControlProvider();
    // return new LabjackAdcLcmClient(GokartLcmChannel.LABJACK_U3_ADC, 0.2);
    // return new JoystickLcmProvider(GokartLcmChannel.JOYSTICK, 0.2);
  }
}
