// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** stores last pose received by lcm client */
// TODO extra value of this class in comparison with GokartPoseLcmClient is limited
public class GokartPoseLcmLidar implements MappedPoseInterface, GokartPoseListener {
  public final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = null;

  public GokartPoseLcmLidar() {
    gokartPoseLcmClient.addListener(this);
  }

  @Override // from MappedPoseInterface
  public Tensor getPose() {
    return Objects.nonNull(gokartPoseEvent) //
        ? gokartPoseEvent.getPose()
        : GokartPoseLocal.INSTANCE.getPose();
  }

  @Override // from MappedPoseInterface
  public void setPose(Tensor pose, Scalar quality) {
    throw new RuntimeException();
  }

  @Override // from MappedPoseInterface
  public GokartPoseEvent getPoseEvent() {
    return gokartPoseEvent;
  }

  @Override // from GokartPoseListener
  public final void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
