// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum RnCurveHelper {
  ;
  /** @param optionalCurve
   * @return if enough elements in curve */
  // function would be obsolete if addAngleToCurve would return Optional<Tensor>
  public static boolean bigEnough(Tensor optionalCurve) {
    return 1 < optionalCurve.length();
  }

  /** @param curve
   * @return appends angle between two following points on the curve */
  public static Tensor addAngleToCurve(Tensor curve) {
    Tensor value = Tensors.empty();
    for (int index = 0; index < curve.length(); ++index) {
      int nextIndex = (index + 1) % curve.length();
      Tensor prev = curve.get(index);
      value.append(prev.append(ArcTan2D.of(curve.get(nextIndex).subtract(prev))));
    }
    return value;
  }
}