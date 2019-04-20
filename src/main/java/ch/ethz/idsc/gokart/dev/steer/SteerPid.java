// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class SteerPid {
  public static final SteerPid GLOBAL = AppResources.load(new SteerPid());
  /***************************************************/
  public Scalar Ki = Quantity.of(1.95, "SCE^-1*SCT*s^-1");
  public Scalar Kp = Quantity.of(3.53, "SCE^-1*SCT");
  public Scalar Kd = Quantity.of(0.57, "SCE^-1*SCT*s");
  public Scalar torqueLimit = Quantity.of(1.5, "SCT");

  /** @return symmetric interval centered at zero that bounds the torque
   * applied to the steering wheel */
  public Clip torqueLimitClip() {
    return Clips.interval(torqueLimit.negate(), torqueLimit);
  }
}
