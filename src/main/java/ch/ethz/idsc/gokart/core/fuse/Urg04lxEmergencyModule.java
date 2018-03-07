// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** sends stop command if front lidar is not operational
 * 
 * Important: The urg04lx lidar is not in use on the gokart.
 * The module has package visibility and may be removed in the future. */
/* package */ class Urg04lxEmergencyModule extends EmergencyModule<RimoPutEvent> implements LidarRayDataListener {
  private static final int WATCHDOG_MS = 400; // 400[ms]
  // ---
  private final Urg04lxLcmClient urg04lxLcmClient = //
      new Urg04lxLcmClient(GokartLcmChannel.URG04LX_FRONT);
  private final Watchdog watchdog = new Watchdog(WATCHDOG_MS * 1e-3);

  @Override // from AbstractModule
  protected void first() throws Exception {
    urg04lxLcmClient.startSubscriptions();
    urg04lxLcmClient.urg04lxDecoder.addRayListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    urg04lxLcmClient.urg04lxDecoder.removeRayListener(this);
    urg04lxLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    watchdog.pacify(); // <- at nominal rate the watchdog is notified every 100[ms]
  }

  @Override // from LidarRayDataListener
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // ---
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(watchdog.isBlown() ? RimoPutEvent.PASSIVE : null);
  }
}
