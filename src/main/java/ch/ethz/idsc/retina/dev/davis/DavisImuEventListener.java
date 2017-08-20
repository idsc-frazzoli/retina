// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis._240c.DavisImuEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;

/** listens to a single imu event which contains
 * a single readout of accel, temperature, or gyro
 * 
 * several of these events are collected into a {@link DavisImuFrame}
 * to which a {@link DavisImuFrameListener} subscribes to */
public interface DavisImuEventListener extends DavisEventListener {
  void imu(DavisImuEvent davisImuEvent);
}
