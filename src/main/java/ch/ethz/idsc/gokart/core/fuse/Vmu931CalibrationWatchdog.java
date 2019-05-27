// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

/** module ensures that VMU931 has carried out calibration procedure since startup of sensor
 * otherwise operation of the gokart motors is suppressed */
public class Vmu931CalibrationWatchdog extends EmergencyModule<RimoPutEvent> implements Vmu931ImuFrameListener {
  private static final double TIMEOUT_S = 0.3;
  // ---
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Watchdog watchdog = SoftWatchdog.barking(TIMEOUT_S);

  private static boolean isCalibrated() {
    Vmu931LcmServerModule vmu931LcmServerModule = //
        ModuleAuto.INSTANCE.getInstance(Vmu931LcmServerModule.class);
    return Objects.nonNull(vmu931LcmServerModule) //
        && vmu931LcmServerModule.isCalibrated();
  }

  @Override // from AbstractModule
  protected void first() {
    vmu931ImuLcmClient.addListener(this);
    vmu931ImuLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    vmu931ImuLcmClient.stopSubscriptions();
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    return isCalibrated() && !watchdog.isBarking() // calibrated and active
        ? Optional.empty()
        : RimoPutEvent.OPTIONAL_RIMO_PASSIVE;
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    watchdog.notifyWatchdog();
  }
}
