// code by vc, jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** the purpose of the class is to carry out the math for the simple obstacle
 * check method. */
// the class name is preliminary
public class SimpleSpacialObstaclePredicate implements SpacialObstaclePredicate {
  /** convenient way for the application layer to obtain an instance
   * without having to specify the geometric configuration
   * 
   * @return */
  public static SpacialObstaclePredicate createVlp16() {
    return new SimpleSpacialObstaclePredicate( //
        SafetyConfig.GLOBAL.vlp16_ZLo, //
        SafetyConfig.GLOBAL.vlp16_ZHi, //
        SensorsConfig.GLOBAL.vlp16_incline);
  }

  // ---
  private final double lo;
  private final double hi;
  private final double inc;

  public SimpleSpacialObstaclePredicate(Scalar vlp16_ZLo, Scalar vlp16_ZHi, Scalar incline) {
    lo = vlp16_ZLo.number().doubleValue();
    hi = vlp16_ZHi.number().doubleValue();
    inc = incline.number().doubleValue();
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(Tensor point) {
    return isObstacle( //
        point.Get(0).number().doubleValue(), //
        point.Get(2).number().doubleValue());
  }

  @Override // from SpacialObstaclePredicate
  public boolean isObstacle(double x, double z) {
    double z_corrected = z - x * inc; // negative sign
    return lo < z_corrected && z_corrected < hi;
  }
}
