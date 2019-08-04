// code by gjoel
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidPlanLcm {
  ;
  /** @param byteBuffer
   * @return clothoid plan */
  public static ClothoidPlan decode(ByteBuffer byteBuffer) {
    Tensor decoded = PursuitPlanLcm.decode(byteBuffer);
    Tensor pose = decoded.get(0);
    Tensor lookAhead = new Se2GroupElement(pose).inverse().combine(decoded.get(1));
    return ClothoidPlan.from(lookAhead, pose, PursuitPlanLcm.decodeIsForward(decoded).orElse(true)).get();
  }
}
