// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.red.Norm;

/** MPCBSplineMap uses MPCBSpline to map control points */
public enum MPCBSplineMap {
  ;
  public static Tensor getPositions(Tensor controlpointsX, Tensor controlpointsY, Tensor queryPositions, boolean circle) {
    Tensor bm = MPCBSpline.getBasisMatrix(controlpointsX.length(), queryPositions, 0, circle);
    return MPCBSplineMap.getPositions(controlpointsX, controlpointsY, queryPositions, circle, bm);
  }

  public static Tensor getPositions(Tensor controlpointsX, Tensor controlpointsY, Tensor queryPositions, boolean circle, Tensor basisMatrix) {
    Tensor posX = basisMatrix.dot(controlpointsX);
    Tensor posY = basisMatrix.dot(controlpointsY);
    return Transpose.of(Tensors.of(posX, posY));
  }

  public static Tensor getSidewardsUnitVectors(Tensor controlpointsX, Tensor controlpointsY, Tensor queryPositions, boolean circle) {
    Tensor matrix = MPCBSpline.getBasisMatrix(controlpointsY.length(), queryPositions, 1, circle);
    return MPCBSplineMap.getSidewardsUnitVectors(controlpointsX, controlpointsY, queryPositions, circle, matrix);
  }

  public static Tensor getSidewardsUnitVectors(Tensor controlpointsX, Tensor controlpointsY, Tensor queryPositions, boolean circle, Tensor basisMatrix1Der) {
    // forward vectors
    Tensor forwardX = basisMatrix1Der.dot(controlpointsX);
    Tensor forwardY = basisMatrix1Der.dot(controlpointsY);
    Tensor normVector = Tensors.vector(i -> RealScalar.ONE.divide(Norm._2.of(Tensors.of(forwardX.Get(i), forwardY.Get(i)))), forwardX.length());
    Tensor unitSideX = forwardY.pmul(normVector);
    Tensor unitSideY = forwardX.negate().pmul(normVector);
    return Transpose.of(Tensors.of(unitSideX, unitSideY));
  }
}
