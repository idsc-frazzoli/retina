// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DavisImuEvent;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;

/** listens to a single imu event which contains a single readout of acceleration,
 * temperature, or gyroscope
 * 
 * several of these events are collected into a {@link DavisImuFrame} to which a
 * {@link DavisImuFrameListener} subscribes to */
@FunctionalInterface
public interface DavisImuListener {
  /** @param davisImuEvent */
  void davisImu(DavisImuEvent davisImuEvent);
}
