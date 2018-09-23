// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum SlamMapMoveUtil {
  ;
  /** compares current position with map boarders and computes a positionDifference by which the map needs to be moved
   * 
   * @param vehiclePosition with units
   * @param corner current lower left corner of map
   * @param cornerHigh current upper right corner of map
   * @param mapMoveVector unitless
   * @param padding [m] if pose is closer than padding to map boarders, we move map
   * @return positionDifference unitless by which the map should be moved */
  public static Tensor computePositionDifference(Tensor vehiclePosition, Tensor corner, //
      Tensor cornerHigh, Tensor mapMoveVector, double padding) {
    Tensor positionDifference = Tensors.vector(0, 0);
    if (vehiclePosition.Get(0).subtract(corner.Get(0)).number().doubleValue() < padding)
      positionDifference = positionDifference.add(Tensors.of(mapMoveVector.Get(0).negate(), RealScalar.of(0)));
    if (vehiclePosition.Get(1).subtract(corner.Get(1)).number().doubleValue() < padding)
      positionDifference = positionDifference.add(Tensors.of(RealScalar.of(0), mapMoveVector.Get(1).negate()));
    if (vehiclePosition.Get(0).subtract(cornerHigh.Get(0)).number().doubleValue() > -padding)
      positionDifference = positionDifference.add(Tensors.of(mapMoveVector.Get(0), RealScalar.of(0)));
    if (vehiclePosition.Get(1).subtract(cornerHigh.Get(1)).number().doubleValue() > -padding)
      positionDifference = positionDifference.add(Tensors.of(RealScalar.of(0), mapMoveVector.Get(1)));
    return positionDifference;
  }
}
