// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// TODO make consistent with DavisImuChannel: either subtract first timestamp, or not?
public class Vmu931ImuChannel implements SingleChannelInterface {
  private Integer time_zero = null;

  @Override // from SingleChannelInterface
  public String channel() {
    return GokartLcmChannel.VMU931_AG;
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
    if (Objects.isNull(time_zero))
      time_zero = vmu931ImuFrame.timestamp_ms();
    return Tensors.of( //
        RealScalar.of(vmu931ImuFrame.timestamp_ms()), //
        vmu931ImuFrame.acceleration().map(Magnitude.ACCELERATION), //
        vmu931ImuFrame.gyroscope().map(Magnitude.PER_SECOND));
  }
}
