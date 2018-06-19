// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.lcm.SimpleLcmClient;

public class DavisImuLcmClient extends SimpleLcmClient<DavisImuFrameListener> {
  private final String cameraId;

  public DavisImuLcmClient(String cameraId) {
    this.cameraId = cameraId;
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return DavisImuFramePublisher.channel(cameraId);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
    listeners.forEach(davisImuFrameListener -> davisImuFrameListener.imuFrame(davisImuFrame));
  }
}
