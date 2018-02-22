// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.SimpleLcmClient;
import ch.ethz.idsc.tensor.Tensor;

/** needs to start listening! */
// TODO class subject to evaluation!
public class GokartPoseClient extends SimpleLcmClient<GokartPoseListener> implements GokartPoseInterface {
  private GokartPoseEvent event;

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    event = new GokartPoseEvent(byteBuffer);
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return GokartLcmChannel.POSE_LIDAR;
  }

  @Override
  public Tensor getPose() {
    return Objects.isNull(event) ? GokartPoseLocal.INSTANCE.getPose() : event.getPose();
  }
}
