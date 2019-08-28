// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum PoseHelper {
  ;
  /** @param pose vector of the form {x[m], y[m], angle[]}
   * @return given pose
   * @throws Exception if given pose is not valid */
  public static Tensor require(Tensor pose) {
    Magnitude.METER.apply(pose.Get(0));
    Magnitude.METER.apply(pose.Get(1));
    Magnitude.ONE.apply(pose.Get(2));
    return VectorQ.requireLength(pose, 3);
  }

  /** Example:
   * PoseHelper.toSE2Matrix(gokartPoseEvent.getPose())
   * 
   * @param pose vector of the form {x[m], y[m], angle[]}
   * @return */
  public static Tensor toSE2Matrix(Tensor pose) {
    return Se2Matrix.of(toUnitless(pose));
  }

  /** @param pose vector of the form {x[m], y[m], angle}
   * @return {x, y, angle} */
  public static Tensor toUnitless(Tensor pose) {
    return Tensors.of( //
        Magnitude.METER.apply(pose.Get(0)), //
        Magnitude.METER.apply(pose.Get(1)), //
        Magnitude.ONE.apply(pose.Get(2)));
  }

  /** @param vector {x, y, angle}
   * @return {x[m], y[m], angle} */
  public static Tensor attachUnits(Tensor vector) {
    return Tensors.of( //
        Quantity.of(vector.Get(0), SI.METER), //
        Quantity.of(vector.Get(1), SI.METER), //
        vector.Get(2));
  }
}
