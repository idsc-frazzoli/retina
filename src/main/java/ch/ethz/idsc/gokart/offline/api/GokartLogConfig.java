// code by jph
package ch.ethz.idsc.gokart.offline.api;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

public class GokartLogConfig {
  /** initial pose
   * 
   * Example: {41.99[m], 49.20[m], 0.4424784}
   * Default: null */
  public Tensor pose = null;

  /***************************************************/
  public Tensor model() {
    return Se2Utils.toSE2Matrix(pose.extract(0, 2).map(Magnitude.METER).append(pose.Get(2)));
  }
}
