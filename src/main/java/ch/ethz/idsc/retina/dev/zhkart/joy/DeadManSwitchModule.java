// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.GetListener;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.PutProvider;
import ch.ethz.idsc.retina.dev.zhkart.fuse.EmergencyModule;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

class RimoDeadMan implements PutProvider<RimoPutEvent> {
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

/** action of emergency module is to brake for 2.5[s] */
// TODO no good: when joystick is missing, immediately brakes regardless of speed
// TODO no good: when speed > threshold, only brakes once but whenever speed > threshold -> repeatedly
public class DeadManSwitchModule extends EmergencyModule<LinmotPutEvent> implements GetListener<RimoGetEvent> {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final Watchdog watchdog_isPresent = new Watchdog(0.2);
  private final Watchdog watchdog_inControl = //
      new Watchdog(JoystickConfig.GLOBAL.deadManPeriodSeconds().number().doubleValue());
  private final RimoDeadMan rimoDeadMan = new RimoDeadMan();
  private Long tic = null; // TODO extract functionality in separate class

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(rimoDeadMan);
    LinmotSocket.INSTANCE.addPutProvider(this);
    joystickLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    joystickLcmClient.stopSubscriptions();
    RimoSocket.INSTANCE.removeGetListener(this);
    RimoSocket.INSTANCE.removePutProvider(rimoDeadMan);
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from GetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    getEvent_process(rimoGetEvent, joystickLcmClient.getJoystick());
  }

  /** @param rimoGetEvent
   * @param optional */
  /* package */ void getEvent_process(RimoGetEvent rimoGetEvent, Optional<JoystickEvent> optional) {
    Tensor pair = rimoGetEvent.getAngularRate_Y_pair();
    Scalar rate = Norm.INFINITY.ofVector(pair); // unit "rad*s^-1"
    Scalar rateThreshold = JoystickConfig.GLOBAL.deadManRate;
    boolean isSpeedSafe = Scalars.lessThan(rate, rateThreshold);
    // ---
    if (optional.isPresent()) {
      watchdog_isPresent.pacify(); // <- joystick is connected
      GokartJoystickInterface joystick = (GokartJoystickInterface) optional.get();
      if (isSpeedSafe || !joystick.isPassive())
        watchdog_inControl.pacify();
    }
    if (watchdog_isPresent.isBlown() || watchdog_inControl.isBlown())
      if (Objects.isNull(tic)) {
        tic = now();
        rimoDeadMan.isBlown = true;
      }
  }

  @Override // from PutProvider
  public Optional<LinmotPutEvent> putEvent() {
    if (Objects.nonNull(tic)) {
      long DURATION_MS = 2000;
      if (now() - tic < DURATION_MS)
        return Optional.of(LinmotPutHelper.operationToRelativePosition(RealScalar.ONE));
    }
    return Optional.empty(); // allow other entity to control brake
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
