// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PlanSRConfig {
  public static final PlanSRConfig GLOBAL = AppResources.load(new PlanSRConfig());
  // ---
  // TODO units
  public Boolean SR_PED_LEGAL = true;
  public Boolean SR_PED_ILLEGAL = false;
  public Scalar pedVelocity = Quantity.of(1.6, SI.VELOCITY);
  public Scalar carVelocity = Quantity.of(10, SI.VELOCITY);
  public Scalar pedRadius = Quantity.of(0.3, SI.METER);
  public Scalar maxAccel = Quantity.of(5.0, SI.ACCELERATION);
  public Scalar reactionTime = Quantity.of(0.6, SI.SECOND);
}
