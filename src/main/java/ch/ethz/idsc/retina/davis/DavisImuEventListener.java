// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DavisImuEvent;
import ch.ethz.idsc.retina.davis._240c.DavisImuFrame;

/** listens to a single imu event which contains
 * a single readout of accel, temperature, or gyro
 * 
 * several of these events for an {@link DavisImuFrame}
 * to which {@link DavisImuFrameListener} subscribe to */
public interface DavisImuEventListener extends DavisEventListener {
  void imu(DavisImuEvent davisImuEvent);
}
