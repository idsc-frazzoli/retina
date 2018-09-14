// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// SLAM estimated curve utils
/* package */ enum SlamCurveUtil {
  ;
  /** computes curvature. Curvature at first and last point cannot be computed and is set to the
   * neighbouring value
   * 
   * @param curve assumed to not have identical points
   * @return curvature Tensor of same length as curve */
  public static Tensor localCurvature(Tensor curve) {
    Tensor prev = curve.get(0);
    Tensor current = curve.get(1);
    Tensor curvature = Tensors.vector(0);
    for (int i = 2; i < curve.length(); ++i) {
      Tensor next = curve.get(i);
      // since curve is interpolated, we know that the 3 points are different from each other
      Tensor localCurvature = SlamCurveExtrapolate.limitCurvature(SignedCurvature2D.of(prev, current, next).get());
      curvature.append(localCurvature);
      prev = current;
      current = next;
    }
    curvature.append(curvature.get(curvature.length() - 1));
    curvature.set(curvature.get(1), 0);
    return curvature;
  }
}
