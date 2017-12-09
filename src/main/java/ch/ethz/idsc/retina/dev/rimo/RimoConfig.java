// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;

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
  // TODO comment on all constants
  public Scalar rateLimit = Quantity.of(20, "rad*s^-1");
  public Scalar Kp = Quantity.of(80, "ARMS*rad^-1*s"); // 40
  public Scalar Ki = Quantity.of(20, "ARMS*rad^-1"); // 15
  public Scalar torqueLimit = Quantity.of(2000, "ARMS");
  public Scalar testPulseLo = Quantity.of(0, "rad*s^-1");
  public Scalar testPulseHi = Quantity.of(50, "rad*s^-1");

  /***************************************************/
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }
}
