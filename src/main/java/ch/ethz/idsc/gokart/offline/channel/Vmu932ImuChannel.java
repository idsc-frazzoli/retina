// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public enum Vmu932ImuChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelInterface
  public String channel() {
    return GokartLcmChannel.VMU932_AG;
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
    return Tensors.of( //
        RealScalar.of(vmu931ImuFrame.timestamp_ms()), //
        vmu931ImuFrame.acceleration().map(Magnitude.ACCELERATION).map(Round._8), //
        vmu931ImuFrame.gyroscope().map(Magnitude.PER_SECOND).map(Round._8));
  }
}
