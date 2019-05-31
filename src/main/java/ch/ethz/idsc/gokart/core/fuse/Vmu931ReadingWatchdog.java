// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** module ensures that VMU931 data is available via LCM */
public class Vmu931ReadingWatchdog extends EmergencyModule<RimoPutEvent> implements Vmu931ImuFrameListener {
  private static final double TIMEOUT_S = 0.3;
  // ---
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Watchdog watchdog = SoftWatchdog.barking(TIMEOUT_S);

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
    return watchdog.isBarking() // calibrated and active
        ? RimoPutEvent.OPTIONAL_RIMO_PASSIVE
        : Optional.empty();
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    watchdog.notifyWatchdog();
  }
}
