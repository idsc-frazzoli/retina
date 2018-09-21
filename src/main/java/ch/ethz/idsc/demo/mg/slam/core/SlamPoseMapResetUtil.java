// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum SlamPoseMapResetUtil {
  ;
  // new corner computation
  public static Tensor computePoseDifference(Tensor pose, Tensor corner, Tensor cornerHigh, Tensor mapMoveVector, double padding) {
    Tensor moveVector = Tensors.vector(0, 0, 0);
    if (pose.Get(0).subtract(corner.Get(0)).number().doubleValue() < padding)
      moveVector = Tensors.of(mapMoveVector.Get(0).negate(), RealScalar.of(0), RealScalar.of(0));
    if (pose.Get(1).subtract(corner.Get(1)).number().doubleValue() < padding)
      moveVector = Tensors.of(RealScalar.of(0), mapMoveVector.Get(1).negate(), RealScalar.of(0));
    if (pose.Get(0).subtract(cornerHigh.Get(0)).number().doubleValue() > -padding)
      moveVector = Tensors.of(mapMoveVector.Get(0), RealScalar.of(0), RealScalar.of(0));
    if (pose.Get(1).subtract(cornerHigh.Get(1)).number().doubleValue() > -padding)
      moveVector = Tensors.of(RealScalar.of(0), mapMoveVector.Get(1), RealScalar.of(0));
    return moveVector;
  }
  
  /** resets the map according to the moved vehicle pose
   * 
   * @param occurrenceMap
   * @param poseDifference difference between current and desired pose after resetting */
  public static void resetMap(MapProvider occurrenceMap, Tensor poseDifference) {
    occurrenceMap.moveMap(poseDifference);
  }
}
