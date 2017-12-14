// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** parameters for PI controller of torque control */
public class JoystickConfig implements Serializable {
  public static final JoystickConfig GLOBAL = AppResources.load(new JoystickConfig());

  private JoystickConfig() {
  }

  /***************************************************/
  /** the physical maximum torque limit is 2316[ARMS]
   * the torque limit is used in {@link RimoTorqueJoystickModule} */
  public Scalar torqueLimit = Quantity.of(2000, "ARMS");
  /** threshold for angular speed beyond which brake will activate
   * by itself if autonomy determines safety issue */
  public Scalar deadManRate = Quantity.of(4.0, "rad*s^-1");
  /** period during which joystick is passive after which an action may be taken */
  public Scalar deadManPeriod = Quantity.of(2.0, "s");
  /** duration of brake */
  public Scalar brakeDuration = Quantity.of(2.2, "s");
  /***************************************************/
  private static final ScalarUnaryOperator TO_SECONDS = QuantityMagnitude.SI().in(Unit.of("s"));

  public Scalar deadManPeriodSeconds() {
    return TO_SECONDS.apply(deadManPeriod);
  }
}
