// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.tensor.Tensor;

public class LinmotGetChannel implements SingleChannelInterface {
  @Override // from SingleChannelTable
  public String channel() {
    return LinmotLcmServer.CHANNEL_GET;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return new LinmotGetEvent(byteBuffer).asVector();
  }
}
