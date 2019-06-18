// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum Se2CurveHelper {
  ;
  /** @param curve
   * @param pose {x, y, heading}
   * @return */
  public static int closest(Tensor curve, Tensor pose) {
    return ArgMin.of(Tensor.of(curve.stream() //
        .map(curvePoint -> Se2ParametricDistance.INSTANCE.distance(curvePoint, pose))));
  }

  /** @param curve
   * @return index of closest point to origin */
  public static int closestEuclid(Tensor curve) {
    return ArgMin.of(Tensor.of(curve.stream() //
        .map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
  }
}
