// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum DavisImuChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelInterface
  public String channel() {
    return DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  }

  @Override // from SingleChannelInterface
  public Tensor row(ByteBuffer byteBuffer) {
    DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
    return Tensors.of( //
        RealScalar.of(davisImuFrame.time_us_raw()), // m2
        davisImuFrame.accelImageFrame().map(Magnitude.ACCELERATION), // m3
        davisImuFrame.temperature().map(Magnitude.DEGREE_CELSIUS), // m4
        davisImuFrame.gyroImageFrame().map(Magnitude.PER_SECOND)); // m5
  }
}
