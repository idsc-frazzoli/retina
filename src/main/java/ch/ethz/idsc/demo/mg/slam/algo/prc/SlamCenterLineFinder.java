// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// utils to find the race track center line from the detected boundaries
/* package */ enum SlamCenterLineFinder {
  ;
  private final static Scalar MAX_OFFSET = RealScalar.of(1); // [m]

  /** offsets the given curve by a constant Tensor offset
   * 
   * @param curve matrix N x 2
   * @param offset vector of length 2
   * @return all points in boundary shifted by given offset */
  public static Tensor constOffsetCurve(Tensor curve, Scalar offset) {
    return Tensor.of(curve.stream().map(p -> p.add(Tensors.of(RealScalar.of(0), offset))));
  }

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
      Tensor localCurvature = SignedCurvature2D.of(prev, current, next).get();
      curvature.append(localCurvature);
      prev = current;
      current = next;
    }
    curvature.append(curvature.get(curvature.length() - 1));
    curvature.set(curvature.get(1), 0);
    return curvature;
  }

  /** computes offset based on curvature at end of curve
   * 
   * @param curve
   * @return */
  public static Scalar computeOffset(Tensor curve, Scalar localCurvature) {
    Scalar offset = localCurvature.multiply(RealScalar.of(3));
    if (Scalars.lessEquals(offset, MAX_OFFSET.negate()))
      return MAX_OFFSET;
    if (Scalars.lessEquals(MAX_OFFSET, offset))
      return MAX_OFFSET.negate();
    return offset.negate();
  }
}
