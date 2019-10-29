// code by mh
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldClip;

public class BrakeFunctionConfig {
  public static final BrakeFunctionConfig GLOBAL = AppResources.load(new BrakeFunctionConfig());
  /***************************************************/
  /** the deceleration threshold after which the braking function is corrected [m/s^2] */
  public Scalar decelerationThreshold = Quantity.of(1, SI.ACCELERATION);
  /** the speed threshold after which the correction is active [m/s] */
  public Scalar speedThreshold = Quantity.of(1, SI.VELOCITY);
  /** the ratio between gokart speed and wheelspeed after which the wheel is considered to be locked up */
  @FieldClip(min = "0", max = "1")
  public Scalar lockupRatio = RealScalar.of(0.6);
  /** the filter for the update */
  @FieldClip(min = "0", max = "1")
  public Scalar geodesicFilterAlpha = RealScalar.of(0.01);
}
