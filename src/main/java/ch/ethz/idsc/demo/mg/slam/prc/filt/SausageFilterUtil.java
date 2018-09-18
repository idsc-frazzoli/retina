// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ enum SausageFilterUtil {
  ;
  /** distance from point to the closest point of the curve
   * 
   * @param point
   * @param curve minimum length 1
   * @return minDistance as root of euclidean norm */
  public static Scalar computeMinDistance(Tensor point, Tensor curve) {
    Scalar minDistance = distanceOfPoints(point, curve.get(0));
    for (int i = 1; i < curve.length(); ++i) {
      Scalar distance = distanceOfPoints(point, curve.get(i));
      if (Scalars.lessEquals(distance, minDistance))
        minDistance = distance;
    }
    return minDistance;
  }

  /** by distance we mean the square root of the euclidean norm of the vector between the two input arguments */
  private static Scalar distanceOfPoints(Tensor firstPoint, Tensor secondPoint) {
    return Sqrt.FUNCTION.apply(Norm2Squared.between(firstPoint, secondPoint));
  }
}
