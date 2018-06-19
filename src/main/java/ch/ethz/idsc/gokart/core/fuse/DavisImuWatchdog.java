// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** the davis imu watchdog detects the absence of {@link DavisImuFrame}
 * for instance when the connection to the Davis240C camera fails. */
public class DavisImuWatchdog extends EmergencyModule<RimoPutEvent> implements DavisImuFrameListener {
  private static final long TIMEOUT_MS = 150; // 150[ms]
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private Watchdog watchdog;

  public DavisImuWatchdog() {
    davisImuLcmClient.addListener(this);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    watchdog = new Watchdog(TIMEOUT_MS * 1e-3);
    davisImuLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    davisImuLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    boolean isBlown = watchdog.isBlown(); // true == stop gokart
    return Optional.ofNullable(isBlown ? RimoPutEvent.PASSIVE : null);
  }

  /***************************************************/
  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    // TODO check whether davis240c is upside down using acceleration ...
    // ... the orientation of the camera is critical for the correct estimation of gyro rate.
    watchdog.pacify();
  }
}
