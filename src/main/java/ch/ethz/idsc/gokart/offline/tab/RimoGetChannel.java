// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.tensor.Tensor;

public class RimoGetChannel implements SingleChannelInterface {
  @Override // from SingleChannelTable
  public String channel() {
    return RimoLcmServer.CHANNEL_GET;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return new RimoGetEvent(byteBuffer).asVector();
  }
}
