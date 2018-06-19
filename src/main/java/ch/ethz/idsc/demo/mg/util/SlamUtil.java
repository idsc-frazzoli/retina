// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Inverse;

// provides static functions used in the SLAM algorithm
public class SlamUtil {
  /** go kart to world transformation
   * 
   * @param pose [x,y,angle] x and y are Quantity.of(METER)
   * @param gokartFramePos coordinates in go kart frame
   * @return Tensor containing homogeneous coordinates in world frame */
  public static Tensor gokartToWorldTensor(Tensor pose, double[] gokartFramePos) {
    Tensor gokart2World = GokartPoseHelper.toSE2Matrix(pose);
    Tensor worldCoord = gokart2World.dot(Tensors.vector(gokartFramePos[0], gokartFramePos[1], 1));
    return worldCoord;
  }

  /** go kart to world transformation
   * 
   * @param pose [x,y,angle] x and y are Quantity.of(METER)
   * @param gokartFramePos coordinates in go kart frame
   * @return array containing coordinates in world frame */
  public static double[] gokartToWorld(Tensor pose, double[] gokartFramePos) {
    Tensor worldCoordTensor = SlamUtil.gokartToWorldTensor(pose, gokartFramePos);
    double[] worldCoord = new double[] { worldCoordTensor.Get(0).number().doubleValue(), worldCoordTensor.Get(1).number().doubleValue() };
    return worldCoord;
  }

  /** world to go kart transformation
   * 
   * @param pose
   * @param worldFramePos coordinates in world frame
   * @return array containing coordinates in go kart frame */
  public static double[] worldToGokart(Tensor pose, double[] worldFramePos) {
    Tensor worldToGokart = Inverse.of(GokartPoseHelper.toSE2Matrix(pose));
    Tensor gokartCoordTensor = worldToGokart.dot(Tensors.vector(worldFramePos[0], worldFramePos[1], 1));
    double[] gokartCoord = new double[] { gokartCoordTensor.Get(0).number().doubleValue(), gokartCoordTensor.Get(1).number().doubleValue() };
    return gokartCoord;
  }
}
