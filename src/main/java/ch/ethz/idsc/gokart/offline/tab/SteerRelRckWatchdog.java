// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.tensor.Scalar;

public class SteerRelRckWatchdog implements OfflineLogListener {
  private boolean fuse = true;

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      if (!steerGetEvent.isRelRckQual() && fuse) {
        fuse = false;
        System.err.println(" \\--> " + time.number().doubleValue() + "[s]");
      }
    }
  }
}
