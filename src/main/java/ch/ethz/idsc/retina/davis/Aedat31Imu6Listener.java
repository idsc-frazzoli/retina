// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis.io.Aedat31Imu6Event;

@FunctionalInterface
public interface Aedat31Imu6Listener {
  void imu6Event(Aedat31Imu6Event aedat31Imu6Event);
}
