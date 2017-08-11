// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis._240c.DavisImuFrame;

public interface DavisImuFrameListener {
  void imuFrame(DavisImuFrame davisImuFrame);
}
