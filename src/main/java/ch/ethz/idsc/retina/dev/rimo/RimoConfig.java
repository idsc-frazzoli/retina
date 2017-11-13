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
  public Scalar Kp = Quantity.of(40, "ARMS*rad^-1*s"); // 40
  public Scalar Ki = Quantity.of(15, "ARMS*rad^-1"); // 15
  public Scalar torqueLimit = Quantity.of(400, "ARMS");

  /***************************************************/
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }
}
