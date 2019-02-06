// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.tensor.Tensor;

public class SteerGetChannel implements SingleChannelInterface {
  @Override // from SingleChannelTable
  public String channel() {
    return SteerLcmServer.CHANNEL_GET;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return new SteerGetEvent(byteBuffer).asVector();
  }
}
