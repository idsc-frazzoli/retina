// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

public enum RimoPutChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelTable
  public String channel() {
    return RimoLcmServer.CHANNEL_PUT;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return RimoPutHelper.from(byteBuffer).getTorque_Y_pair().map(Magnitude.ARMS);
  }
}
