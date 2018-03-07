// code by vc, jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** the purpose of the class is to carry out the math for the
 * simple obstacle check method. */
// the class name is preliminary
public class SimpleSpacialObstaclePredicate implements SpacialObstaclePredicate {
  // TODO VC the createVlp16() is a convenient way for the application layer to obtain
  // an instance without having to specify parameters repeatedly:
  public static SpacialObstaclePredicate createVlp16() {
    // return new SimpleSpacialObstaclePredicate(
    // vlp16_ZLo, // take from SafetyConfig.GLOBAL.
    // vlp16_ZHi, // take from SafetyConfig.GLOBAL.
    // incline); // take from SensorsConfig.GLOBAL
    return null; // <- obsolete once above return statement works
  }

  // ---
  // members:
  // private final double lo;
  // private final double hi;
  // private final double incline;
  public SimpleSpacialObstaclePredicate(Scalar vlp16_ZLo, Scalar vlp16_ZHi, Scalar incline) {
    // TODO assign lo hi inc
  }

  @Override
  public boolean isObstacle(Tensor x) {
    // TODO check x,y,z according to lo,hi,inc
    return true;
  }
}
