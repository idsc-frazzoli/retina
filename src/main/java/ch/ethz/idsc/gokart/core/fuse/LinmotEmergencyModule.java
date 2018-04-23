// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** sends stop command if either of the conditions was true
 * 1) linmot messages have not been received for a timeout period, e.g. 50[ms]
 * 2) linmot status is "not-operational", see {@link LinmotGetEvent#isOperational()}
 * 
 * <p>The module needs to be started after linmot calibration procedure otherwise
 * the linmot will not read "operational"
 * 
 * <p>Important: During operation of the gokart, it was observed that the linmot
 * enters the not-operational state without apparent reason typically 10-20 minutes
 * into the trial. Subsequent to that event, the {@link LinmotEmergencyModule}
 * prevents the gokart from further accelerating. In one instance, the linmot
 * stopped sending status messages altogether (20ms after failure!).
 * 
 * Temporary solution: do not activate LinmotEmergencyModule, but beware that
 * braking by joystick may not be operational. Deceleration can still be accomplished by
 * 1) applying opposite motor torque, and
 * 2) pressing the brake by foot */
public final class LinmotEmergencyModule extends EmergencyModule<RimoPutEvent> implements LinmotGetListener {
  /** the micro-autobox sends messages at 250[Hz], i.e. at intervals of 4[ms] */
  private static final long TIMEOUT_MS = 50;
  // ---
  private Watchdog watchdog;
  private boolean isBlown = false;

  @Override // from AbstractModule
  protected void first() throws Exception {
    watchdog = new Watchdog(TIMEOUT_MS * 1e-3);
    LinmotSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    // permanent off
    watchdog.pacify(); // <- at nominal rate the watchdog is notified every 4[ms]
    isBlown |= !linmotGetEvent.isOperational();
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    isBlown |= watchdog.isBlown(); // if status of linmot was not established for a timeout period
    return Optional.ofNullable(isBlown ? RimoPutEvent.PASSIVE : null); // deactivate throttle
  }
}
