// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class DavisImuChannel implements SingleChannelInterface {
  private Integer time_zero = null;

  @Override // from SingleChannelInterface
  public String channel() {
    return DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
    if (Objects.isNull(time_zero))
      time_zero = davisImuFrame.time_us_raw();
    return Tensors.of( //
        davisImuFrame.getTimeRelativeTo(time_zero).map(Magnitude.SECOND), // m2
        davisImuFrame.accelImageFrame().map(Magnitude.ACCELERATION), // m3
        davisImuFrame.temperature().map(Magnitude.DEGREE_CELSIUS), // m4
        davisImuFrame.gyroImageFrame().map(Magnitude.PER_SECOND)); // m5
  }
}
