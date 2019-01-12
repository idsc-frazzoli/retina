// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class GokartPoseLcmClient extends SimpleLcmClient<GokartPoseListener> {
  public GokartPoseLcmClient() {
    super(GokartLcmChannel.POSE_LIDAR);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    GokartPoseEvent event = new GokartPoseEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }
}
