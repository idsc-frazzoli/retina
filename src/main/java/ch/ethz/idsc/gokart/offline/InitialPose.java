// code by jph
package ch.ethz.idsc.gokart.offline;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

public class InitialPose {
  public Tensor pose;

  /***************************************************/
  public Tensor model() {
    return Se2Utils.toSE2Matrix(pose.extract(0, 2).map(Magnitude.METER).append(pose.Get(2)));
  }
}
