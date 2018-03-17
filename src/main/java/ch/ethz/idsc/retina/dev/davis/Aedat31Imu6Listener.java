// code by jph
package ch.ethz.idsc.retina.dev.davis;

import ch.ethz.idsc.retina.dev.davis.io.Aedat31Imu6Event;

public interface Aedat31Imu6Listener {
  void imu6Event(Aedat31Imu6Event aedat31Imu6Event);
}
