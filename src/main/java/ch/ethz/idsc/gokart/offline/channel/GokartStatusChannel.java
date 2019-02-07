// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.tensor.Tensor;

public class GokartStatusChannel implements SingleChannelInterface {
  @Override // from SingleChannelTable
  public String channel() {
    return GokartLcmChannel.STATUS;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return new GokartStatusEvent(byteBuffer).asVector();
  }
}
