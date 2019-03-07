// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.dev.u3.GokartLabjackLcmClient;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PI controller of torque control */
public class ManualConfig {
  public static final ManualConfig GLOBAL = AppResources.load(new ManualConfig());
  /***************************************************/
  /** the physical maximum torque limit is 2316[ARMS]
   * the torque limit is used in {@link RimoTorqueManualModule} */
  public final Scalar torqueLimit = Quantity.of(2315, NonSI.ARMS);
  public final Scalar timeout = Quantity.of(0.2, SI.SECOND);

  /***************************************************/
  /** @return clip interval for permitted torque */
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }

  /** .
   * ante 20181211: the GenericXboxPad joystick was used
   * post 20181211: throttle pedal and boost button
   * 
   * @return manual control as configured on the gokart */
  public ManualControlProvider createProvider() {
    return new GokartLabjackLcmClient(GokartLcmChannel.LABJACK_U3_ADC, Magnitude.SECOND.toDouble(timeout));
  }
}
