// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;

/** sends stop command if steer angle is not calibrated or steer angle tracking is unhealthy */
public final class SteerCalibrationWatchdog extends EmergencyModule<RimoPutEvent> {
  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return create(SteerSocket.INSTANCE.getSteerColumnTracker());
  }

  static Optional<RimoPutEvent> create(SteerColumnTracker steerColumnTracker) {
    return steerColumnTracker.isCalibratedAndHealthy() //
        ? Optional.empty()
        : StaticHelper.OPTIONAL_RIMO_PASSIVE; // deactivate throttle
  }
}
