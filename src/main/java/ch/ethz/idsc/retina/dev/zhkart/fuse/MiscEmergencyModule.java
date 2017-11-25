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

/** sends stop command if steer battery voltage is outside of valid range
 * or if emergency flag is set in {@link MiscGetEvent} */
public class MiscEmergencyModule extends AbstractModule implements MiscGetListener, RimoPutProvider {
  private static final long VOLTAGE_TIMEOUT_MS = 400;
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

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    isBlown |= watchdog_steerVoltage.isBlown();
    return Optional.ofNullable(isBlown ? RimoPutEvent.STOP : null);
  }

  @Override // from MiscGetListener
  public void getEvent(MiscGetEvent miscGetEvent) {
    if (SteerConfig.GLOBAL.operatingVoltageClip().isInside(miscGetEvent.getSteerBatteryVoltage()))
      watchdog_steerVoltage.pacify();
    isBlown |= miscGetEvent.isEmergency(); // comm timeout, or manual switch
  }
}
