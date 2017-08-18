// code by jph
package ch.ethz.idsc.retina.app;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class DavisImuFramePublisher implements DavisImuFrameListener {
  public static final String IMU_CHANNEL = "davis.id.imu";
  // ---
  private final LCM lcm = LCM.getSingleton();

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    int length = davisImuFrame.length();
    // ---
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = new byte[length];
    davisImuFrame.get(ByteBuffer.wrap(binaryBlob.data));
    lcm.publish(IMU_CHANNEL, binaryBlob);
  }
}
