// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import idsc.DavisImu;
import lcm.lcm.LCM;

class DavisImuFramePublisher implements DavisImuFrameListener {
  public static final String IMU_CHANNEL = "davis.id.imu";
  // ---
  private final LCM lcm = LCM.getSingleton();

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    DavisImu davisImu = new DavisImu();
    davisImu.clock_usec = davisImuFrame.time;
    davisImu.accel[0] = davisImuFrame.accelX;
    davisImu.accel[1] = davisImuFrame.accelY;
    davisImu.accel[2] = davisImuFrame.accelZ;
    davisImu.temperature = davisImuFrame.temperature;
    davisImu.gyro[0] = davisImuFrame.gyroX;
    davisImu.gyro[1] = davisImuFrame.gyroY;
    davisImu.gyro[2] = davisImuFrame.gyroZ;
    lcm.publish(IMU_CHANNEL, davisImu);
  }
}
