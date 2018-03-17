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
  // TODO VC the createVlp16() is a convenient way for the application layer to
  // obtain
  // an instance without having to specify parameters repeatedly:
  public static SpacialObstaclePredicate createVlp16() {
    return new SimpleSpacialObstaclePredicate(SafetyConfig.GLOBAL.vlp16_ZLo, // take from SafetyConfig.GLOBAL.
        SafetyConfig.GLOBAL.vlp16_ZHi, // take from SafetyConfig.GLOBAL.
        SensorsConfig.GLOBAL.vlp16_incline); // take from SensorsConfig.GLOBAL
  }

  // ---
  // members:
  private final double lo;
  private final double hi;
  private final double inc;

  public SimpleSpacialObstaclePredicate(Scalar vlp16_ZLo, Scalar vlp16_ZHi, Scalar incline) {
    this.lo = vlp16_ZLo.number().doubleValue();
    this.hi = vlp16_ZHi.number().doubleValue();
    this.inc = incline.number().doubleValue();
    // TODO assign lo hi inc
  }

  @Override
  public boolean isObstacle(Tensor x) {
    double z = x.Get(2).number().doubleValue() + x.Get(0).number().doubleValue() * inc;
    if (z > lo && z < hi) {
      return true;
    } else
      return false;
  }
}
