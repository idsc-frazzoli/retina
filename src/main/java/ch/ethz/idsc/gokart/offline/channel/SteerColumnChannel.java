// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

public enum SteerColumnChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelTable
  public String channel() {
    return GokartLcmChannel.STATUS;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return new SteerColumnEvent(byteBuffer).asVector().map(Round._8);
  }
}
