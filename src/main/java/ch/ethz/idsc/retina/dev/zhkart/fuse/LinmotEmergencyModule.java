// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** sends stop command if either of the conditions was true
 * 1) linmot messages have not been received for a timeout period, e.g. 50[ms]
 * 2) linmot status is "not-operational"
 * 3) linmot winding temperature is outside valid range
 * 
 * module needs to be started after linmot calibration procedure otherwise
 * the linmot will not read "operational" */
public final class LinmotEmergencyModule extends AbstractModule implements LinmotGetListener, RimoPutProvider {
  /** the micro-autobox sends messages at 250[Hz], i.e. at intervals of 4[ms] */
  private static final long LINMOT_TIMEOUT_MS = 50;
  // ---
  private final Watchdog watchdog = new Watchdog(LINMOT_TIMEOUT_MS * 1e-3);
  private boolean isBlown = false;

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    watchdog.pacify(); // <- at nominal rate the watchdog is notified every 4[ms]
    isBlown |= !linmotGetEvent.isOperational();
    isBlown |= !linmotGetEvent.isSafeWindingTemperature1();
    isBlown |= !linmotGetEvent.isSafeWindingTemperature2();
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    isBlown |= watchdog.isBlown(); // if status of linmot was not established for a timeout period
    return Optional.ofNullable(isBlown ? RimoPutEvent.STOP : null); // deactivate throttle
  }
}
