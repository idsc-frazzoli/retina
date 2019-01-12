// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;

public class DavisImuLcmClient extends SimpleLcmClient<DavisImuFrameListener> {
  public DavisImuLcmClient(String cameraId) {
    super(DavisImuFramePublisher.channel(cameraId));
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
    listeners.forEach(davisImuFrameListener -> davisImuFrameListener.imuFrame(davisImuFrame));
  }
}
