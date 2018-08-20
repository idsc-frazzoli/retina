// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.io.Serializable;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PI controller of torque control */
public class JoystickConfig implements Serializable {
  public static final JoystickConfig GLOBAL = AppResources.load(new JoystickConfig());
  /***************************************************/
  /** the physical maximum torque limit is 2316[ARMS]
   * the torque limit is used in {@link RimoTorqueJoystickModule} */
  public Scalar torqueLimit = Quantity.of(2315, NonSI.ARMS);
  /** threshold for angular speed beyond which brake will activate
   * by itself if autonomy determines safety issue */
  public Scalar deadManRate = Quantity.of(4.0, "rad*s^-1");
  /** period during which joystick is passive after which an action may be taken */
  public Scalar deadManPeriod = Quantity.of(2.0, SI.SECOND);
  /** duration of brake */
  public Scalar brakeDuration = Quantity.of(2.2, SI.SECOND);

  /***************************************************/
  public Scalar deadManPeriodSeconds() {
    return Magnitude.SECOND.apply(deadManPeriod);
  }

  public Scalar brakeDurationSeconds() {
    return Magnitude.SECOND.apply(brakeDuration);
  }

  public boolean isSpeedSlow(Tensor getAngularRate_Y_pair) {
    Scalar rate = Norm.INFINITY.ofVector(getAngularRate_Y_pair); // unit "rad*s^-1"
    return Scalars.lessThan(rate, JoystickConfig.GLOBAL.deadManRate);
  }

  /** @return clip interval for permitted torque */
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }

  public JoystickLcmProvider createProvider() {
    // only joystick events aged less equals 200[ms] are provided to the application layer
    return new JoystickLcmProvider(GokartLcmChannel.JOYSTICK, 200);
  }
}
