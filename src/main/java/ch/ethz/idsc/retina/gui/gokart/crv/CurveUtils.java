// code by jph
package ch.ethz.idsc.retina.gui.gokart.crv;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public enum CurveUtils {
  ;
  public static final int NO_MATCH = -1;

  public static int closestCloserThan(Tensor beacons, Scalar dist) {
    int best = NO_MATCH;
    for (int index = 0; index < beacons.length(); ++index) {
      Tensor local = beacons.get(index);
      Scalar norm = Norm._2.of(local);
      if (Scalars.lessThan(norm, dist)) {
        dist = norm;
        best = index;
      }
    }
    return best;
  }
}
