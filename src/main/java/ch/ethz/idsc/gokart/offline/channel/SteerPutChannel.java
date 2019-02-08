// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.tensor.Tensor;

public enum SteerPutChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelTable
  public String channel() {
    return SteerLcmServer.CHANNEL_PUT;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return SteerPutEvent.from(byteBuffer).asVector();
  }
}
