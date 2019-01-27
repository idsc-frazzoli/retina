// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.red.Norm;

/** uses UniformBSpline2 to map control points */
/* package */ enum BSplineUtil {
  ;
  // private static Tensor getPositions(Tensor controlpointsX, Tensor controlpointsY, Tensor queryPositions, boolean circle) {
  // Tensor bm = UniformBSpline2.getBasisMatrix(controlpointsX.length(), queryPositions, 0, circle);
  // return getPositions(controlpointsX, controlpointsY, queryPositions, circle, bm);
  // }
  // public static Tensor getPositions(Tensor controlpoints, Tensor basisMatrix) {
  // return basisMatrix.dot(controlpoints);
  // }
  // private static Tensor getSidewardsUnitVectors(Tensor controlpointsX, Tensor controlpointsY, Tensor queryPositions, boolean circle) {
  // Tensor matrix = UniformBSpline2.getBasisMatrix(controlpointsY.length(), queryPositions, 1, circle);
  // return getSidewardsUnitVectors(controlpointsX, controlpointsY, queryPositions, circle, matrix);
  // }
  public static Tensor getSidewardsUnitVectors(Tensor controlpointsX, Tensor controlpointsY, Tensor basisMatrix1Der) {
    // forward vectors
    Tensor forwardX = basisMatrix1Der.dot(controlpointsX);
    Tensor forwardY = basisMatrix1Der.dot(controlpointsY);
    // Tensor tensor = Transpose.of(Tensors.of(forwardX, forwardY));
    // TODO JPH/MH can use Cross2D and Normalize on the stream of vectors but sign is flipped at the moment
    // return Tensor.of(tensor.stream().map(Cross2D::of).map(NormalizeUnlessZero.with(Norm._2)));
    Tensor normVector = Tensors.vector(i -> RealScalar.ONE.divide(Norm._2.of(Tensors.of(forwardX.Get(i), forwardY.Get(i)))), forwardX.length());
    Tensor unitSideX = forwardY.pmul(normVector);
    Tensor unitSideY = forwardX.negate().pmul(normVector);
    return Transpose.of(Tensors.of(unitSideX, unitSideY));
  }
}
