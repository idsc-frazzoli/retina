// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** sends stop command if front lidar is not operational */
public final class Urg04lxEmergencyModule extends AbstractModule implements LidarRayDataListener, RimoPutProvider {
  private static final int WATCHDOG_MS = 400; // 400[ms]
  // ---
  private final Urg04lxLcmClient urg04lxLcmClient = //
      new Urg04lxLcmClient(GokartLcmChannel.URG04LX_FRONT);
  private final Watchdog watchdog = new Watchdog(WATCHDOG_MS * 1e-3);

  @Override
  protected void first() throws Exception {
    urg04lxLcmClient.startSubscriptions();
    urg04lxLcmClient.urg04lxDecoder.addRayListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    urg04lxLcmClient.urg04lxDecoder.removeRayListener(this);
    urg04lxLcmClient.stopSubscriptions();
  }

  @Override // from LidarRayDataListener
  public void timestamp(int usec, int type) {
    watchdog.pacify(); // <- at nominal rate the watchdog is notified every 100[ms]
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // ---
  }

  /***************************************************/
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(watchdog.isBlown() ? RimoPutEvent.STOP : null);
  }
}
