// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum StaticHelper {
  ;
  /** FUNCTION IS NOT IN USE
   * 
   * @param curve_local without any intersections
   * @param dist
   * @return */
  /* package */ public static int closestCloserThan(Tensor curve_local, Scalar dist) {
    int best = CurveUtils.NO_MATCH;
    for (int index = 0; index < curve_local.length(); ++index) {
      Scalar norm = Norm._2.of(curve_local.get(index)); // vector in local coordinates
      if (Scalars.lessThan(norm, dist)) {
        dist = norm;
        best = index;
      }
    }
    return best;
  }
}
