// code by gjoel
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidPlanLcm {
  ;
  /** @param byteBuffer
   * @param isForward whether vehicle drives forward
   * @return clothoid plan */
  public static ClothoidPlan decode(ByteBuffer byteBuffer, boolean isForward) {
    Tensor decoded = PursuitPlanLcm.decode(byteBuffer);
    return ClothoidPlan.from(decoded.get(1), decoded.get(0), isForward).get();
  }
}
