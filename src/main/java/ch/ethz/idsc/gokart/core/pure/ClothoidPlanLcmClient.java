// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.ClothoidPlanLcm;

public class ClothoidPlanLcmClient extends SimpleLcmClient<ClothoidPlanListener> {
  public ClothoidPlanLcmClient() {
    super(GokartLcmChannel.PURSUIT_PLAN);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    ClothoidPlan clothoidPlan = ClothoidPlanLcm.decode(byteBuffer);
    listeners.forEach(clothoidPlanListener -> clothoidPlanListener.planReceived(clothoidPlan));
  }
}
