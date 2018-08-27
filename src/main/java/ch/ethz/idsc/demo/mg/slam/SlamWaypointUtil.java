// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;

public enum SlamWaypointUtil {
  ;
  /** transforms between world and go kart frame
   * 
   * @param worldPosition position of point in world frame
   * @param pose unitless representation go kart pose
   * @return position in go kart frame */
  public static double[] computeGokartPosition(double[] worldPosition, Tensor pose) {
    Tensor gokartPosition = new Se2Bijection(pose).inverse() //
        .apply(Tensors.vectorDouble(worldPosition));
    return Primitives.toDoubleArray(gokartPosition);
  }
}
