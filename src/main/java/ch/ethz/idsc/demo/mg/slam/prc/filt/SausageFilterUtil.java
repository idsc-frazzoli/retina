// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum SausageFilterUtil {
  ;
  /** distance from point to the closest point of the curve
   * 
   * @param point
   * @param curve minimum length 1
   * @return minDistance as root of Euclidean norm
   * @throws Exception if curve is empty */
  public static Scalar computeMinDistance(Tensor point, Tensor curve) {
    Scalar minDistance = Norm._2.between(point, curve.get(0));
    for (int i = 1; i < curve.length(); ++i) {
      Scalar distance = Norm._2.between(point, curve.get(i));
      if (Scalars.lessEquals(distance, minDistance))
        minDistance = distance;
    }
    return minDistance;
  }
}
