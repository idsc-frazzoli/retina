// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.planar.Extract2D;
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

  public static int closestEuclid(Tensor curve) {
    return ArgMin.of(Tensor.of(curve.stream() //
        .map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
  }
}
