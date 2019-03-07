// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class RimoGetLcmClient extends SimpleLcmClient<RimoGetListener> {
  private static boolean notify_flag = true;

  public RimoGetLcmClient() {
    super(RimoLcmServer.CHANNEL_GET);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    try {
      // the protocol has changed from speed control to torque control
      // previous log files will produce an error
      RimoGetEvent event = new RimoGetEvent(byteBuffer);
      listeners.forEach(listener -> listener.getEvent(event));
    } catch (Exception exception) {
      if (notify_flag) {
        notify_flag = false;
        System.err.println("RimoGetLcmClient protocol change");
      }
    }
  }
}
