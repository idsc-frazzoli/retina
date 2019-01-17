// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** the davis imu watchdog detects the absence of {@link DavisImuFrame}
 * for instance when the connection to the Davis240C camera fails. */
public class DavisImuTrackerModule extends EmergencyModule<RimoPutEvent> implements DavisImuFrameListener {
  /** duration of tolerated absence of imu measurements */
  private static final double TIMEOUT_S = 0.5;
  // ---
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final Watchdog watchdog = SoftWatchdog.barking(TIMEOUT_S);

  public DavisImuTrackerModule() {
    davisImuLcmClient.addListener(this);
    davisImuLcmClient.addListener(DavisImuTracker.INSTANCE);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    davisImuLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    davisImuLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return watchdog.isBarking() //
        ? StaticHelper.OPTIONAL_RIMO_PASSIVE
        : Optional.empty();
  }

  /***************************************************/
  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    watchdog.notifyWatchdog();
  }
}
