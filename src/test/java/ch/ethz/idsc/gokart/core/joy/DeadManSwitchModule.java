// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.EmergencyModule;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.util.data.TriggeredTimeInterval;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.tensor.RealScalar;

class RimoDeadMan implements RimoPutProvider {
  volatile boolean isBlown = false;

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(isBlown ? RimoPutEvent.PASSIVE : null);
  }
}

/** module requires the presence of a joystick
 * 
 * action of emergency module is to brake for 2.5[s] */
class DeadManSwitchModule extends EmergencyModule<LinmotPutEvent> implements RimoGetListener {
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  private final Watchdog watchdog_isPresent = new Watchdog(0.2);
  private final Watchdog watchdog_inControl = //
      new Watchdog(JoystickConfig.GLOBAL.deadManPeriodSeconds().number().doubleValue());
  private final RimoDeadMan rimoDeadMan = new RimoDeadMan();
  private final TriggeredTimeInterval triggeredTimeInterval = //
      new TriggeredTimeInterval(JoystickConfig.GLOBAL.brakeDurationSeconds().number().doubleValue());

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(rimoDeadMan);
    LinmotSocket.INSTANCE.addPutProvider(this);
    joystickLcmProvider.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    joystickLcmProvider.stopSubscriptions();
    RimoSocket.INSTANCE.removeGetListener(this);
    RimoSocket.INSTANCE.removePutProvider(rimoDeadMan);
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from GetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    getEvent_process(rimoGetEvent, joystickLcmProvider.getJoystick());
  }

  /** @param rimoGetEvent
   * @param optional */
  /* package */ void getEvent_process(RimoGetEvent rimoGetEvent, Optional<JoystickEvent> optional) {
    boolean isSpeedSafe = JoystickConfig.GLOBAL.isSpeedSlow(rimoGetEvent.getAngularRate_Y_pair());
    // ---
    if (optional.isPresent()) {
      watchdog_isPresent.pacify(); // <- joystick is connected
      GokartJoystickInterface joystick = (GokartJoystickInterface) optional.get();
      if (isSpeedSafe || !joystick.isPassive())
        watchdog_inControl.pacify();
    }
    if (watchdog_isPresent.isBlown() || watchdog_inControl.isBlown()) {
      triggeredTimeInterval.panic();
      rimoDeadMan.isBlown = true;
    }
  }

  @Override // from PutProvider
  public Optional<LinmotPutEvent> putEvent() {
    if (triggeredTimeInterval.isActive())
      return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.ONE));
    return Optional.empty(); // allow other entity to control brake
  }
}
