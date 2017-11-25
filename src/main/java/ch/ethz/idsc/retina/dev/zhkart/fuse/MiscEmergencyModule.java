// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** sends stop command if either of the conditions is met
 * 1) steer battery voltage is outside of valid range for a certain duration
 * 2) the emergency flag is set in {@link MiscGetEvent} */
public final class MiscEmergencyModule extends AbstractModule implements MiscGetListener, RimoPutProvider {
  /** the steering motor is powered through a separate battery.
   * due to abrupt maneuvers that require peak power consumption
   * we tolerate voltage drops below a threshold for a short period of time */
  private static final long VOLTAGE_TIMEOUT_MS = 200;
  // ---
  private final Watchdog watchdog_steerVoltage = new Watchdog(VOLTAGE_TIMEOUT_MS * 1e-3);
  private boolean isBlown = false;

  @Override
  protected void first() throws Exception {
    MiscSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    MiscSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from MiscGetListener
  public void getEvent(MiscGetEvent miscGetEvent) {
    if (SteerConfig.GLOBAL.operatingVoltageClip().isInside(miscGetEvent.getSteerBatteryVoltage()))
      watchdog_steerVoltage.pacify(); // <- at nominal rate the watchdog is notified every 4[ms]
    isBlown |= miscGetEvent.isEmergency(); // comm timeout, or manual switch
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    isBlown |= watchdog_steerVoltage.isBlown();
    return Optional.ofNullable(isBlown ? RimoPutEvent.STOP : null); // deactivate throttle
  }
}
