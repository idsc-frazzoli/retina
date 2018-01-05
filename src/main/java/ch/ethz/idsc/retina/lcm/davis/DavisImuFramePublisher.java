// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public class DavisImuFramePublisher implements DavisImuFrameListener {
  /** @param cameraId
   * @return imu channel name for given serial number of davis camera */
  public static String channel(String cameraId) {
    // the extension "atg" represents: acceleration, temperature, gyro
    return DavisLcmStatics.CHANNEL_PREFIX + "." + cameraId + ".atg";
  }
  // ---

  private final String channel;
  private final BinaryBlobPublisher binaryBlobPublisher;

  public DavisImuFramePublisher(String cameraId) {
    channel = channel(cameraId);
    binaryBlobPublisher = new BinaryBlobPublisher(channel);
  }

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    binaryBlobPublisher.accept(davisImuFrame.asArray());
  }
}
