// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.tensor.Scalar;

/** finds first occurrence of steer unit failure in log file */
public class SteerRelRckWatchdog implements OfflineLogListener {
  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      if (!steerGetEvent.isRelRckQual()) {
        System.err.println(" \\--> " + time.number().doubleValue() + "[s]");
        throw OfflineLogPlayer.endOfFile();
      }
    }
  }
}
