// code by jph
package ch.ethz.idsc.gokart.core.ekf;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class GokartVelocityLcmClient extends SimpleLcmClient<GokartVelocityListener> {
  public GokartVelocityLcmClient() {
    super(GokartLcmChannel.VELOCITY_FUSION);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    GokartVelocityEvent event = new GokartVelocityEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }
}
