// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.ClothoidPlanLcm;

public class ClothoidPlanLcmClient extends SimpleLcmClient<ClothoidPlanListener> {
  private boolean isForward = true;

  public ClothoidPlanLcmClient() {
    super(GokartLcmChannel.PURSUIT_PLAN);
  }

  /** @param isForward whether vehicle drives forward */
  public void setDirection(boolean isForward) {
    this.isForward = isForward;
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    ClothoidPlan clothoidPlan = ClothoidPlanLcm.decode(byteBuffer, isForward);
    listeners.forEach(clothoidPlanListener -> clothoidPlanListener.planReceived(clothoidPlan));
  }
}
