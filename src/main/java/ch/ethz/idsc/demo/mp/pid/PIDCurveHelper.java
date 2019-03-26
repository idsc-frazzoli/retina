package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

enum CurveHelper {
  ;
  /**
   * Returns position of the closest point on the curve to the current pose
   * @param curve
   * @param pose
   * @return point
   */
  static int closest(Tensor curve, Tensor pose) {
    return ArgMin.of(Tensor.of(curve.stream().map(row -> Norm._2.between(row, pose))));
  }

  /**
   * Returns angle between two following points of the closest point on the curve to the current pose
   * @param curve
   * @param point
   * @return
   */
  static Tensor trajAngle(Tensor curve, Tensor point) {
    int index = closest(curve, point);
    int nextIndex = index + 1;
    if (nextIndex > curve.length()) // TODO MCP Write this better
      nextIndex = 0;
    return ArcTan2D.of(curve.get(nextIndex).subtract(curve.get(index)));
  }

  /**
   * Checkes if enough elements in curve
   * @param optionalCurve
   * @return
   */
  static boolean bigEnough(Optional<Tensor> optionalCurve) {
    return optionalCurve.get().length() > 1; // TODO MCP Write this better
  }
}