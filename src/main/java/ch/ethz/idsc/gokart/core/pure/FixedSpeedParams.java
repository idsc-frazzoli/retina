// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldClip;

public class FixedSpeedParams {
  public static final FixedSpeedParams GLOBAL = AppResources.load(new FixedSpeedParams());
  // ---
  @FieldClip(min = "0[m*s^-1]", max = "10[m*s^-1]")
  public Scalar speed = Quantity.of(2, SI.VELOCITY);
}
