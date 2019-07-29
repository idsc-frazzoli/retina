// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

/** Provides information about the absolute position of the steering column */
public class SteerColumnLcmClient extends SimpleLcmClient<SteerColumnListener> {
  public SteerColumnLcmClient() {
    super(GokartLcmChannel.STATUS);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    SteerColumnEvent event = new SteerColumnEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }
}
