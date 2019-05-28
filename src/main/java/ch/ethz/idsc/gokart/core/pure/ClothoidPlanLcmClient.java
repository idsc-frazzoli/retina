// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.gokart.lcm.mod.PursuitPlanLcm;
import ch.ethz.idsc.tensor.Tensor;

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
    Tensor decoded = PursuitPlanLcm.decode(byteBuffer);
    Optional<ClothoidPlan> optional = ClothoidPlan.from(decoded.get(1), decoded.get(0), isForward);
    optional.ifPresent(clothoidPlan -> //
        listeners.forEach(clothoidPlanListener -> clothoidPlanListener.planReceived(clothoidPlan)));
  }
}
