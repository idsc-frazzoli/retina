// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscGetListener;
import ch.ethz.idsc.gokart.dev.misc.MiscSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.data.HardWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** sends stop command if the steer battery voltage is outside of valid range for a certain duration */
public final class SteerBatteryWatchdog extends EmergencyModule<RimoPutEvent> implements MiscGetListener {
  /** the steering motor is powered through a separate battery.
   * due to abrupt maneuvers that require peak power consumption
   * we tolerate voltage drops below a threshold for a short period of time */
  private static final long VOLTAGE_TIMEOUT_MS = 1000; // 1[s] below threshold
  // ---
  private final Watchdog watchdog = HardWatchdog.notified(VOLTAGE_TIMEOUT_MS * 1e-3);
  private boolean isBlown = false;

  @Override // from AbstractModule
  protected void first() throws Exception {
    MiscSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    MiscSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from MiscGetListener
  public void getEvent(MiscGetEvent miscGetEvent) {
    if (SteerConfig.GLOBAL.operatingVoltageClip().isInside(miscGetEvent.getSteerBatteryVoltage()))
      watchdog.notifyWatchdog(); // <- at nominal rate the watchdog is notified every 4[ms]
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    isBlown |= watchdog.isBarking();
    return isBlown //
        ? StaticHelper.OPTIONAL_RIMO_PASSIVE
        : Optional.empty();
  }
}
