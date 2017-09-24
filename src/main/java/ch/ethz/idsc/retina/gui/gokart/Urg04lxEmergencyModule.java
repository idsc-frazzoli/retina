// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** sends stop command if front lidar is not operational */
public class Urg04lxEmergencyModule extends AbstractModule implements LidarRayDataListener, RimoPutProvider {
  private static final int WATCHDOG = 500;
  private final Urg04lxLcmClient urg04lxLcmClient = new Urg04lxLcmClient("front");

  @Override
  protected void first() throws Exception {
    urg04lxLcmClient.startSubscriptions();
    urg04lxLcmClient.urg04lxDecoder.addRayListener(this);
    RimoSocket.INSTANCE.addProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeProvider(this);
    urg04lxLcmClient.urg04lxDecoder.removeRayListener(this);
    urg04lxLcmClient.stopSubscriptions();
  }

  private long now = 0;

  @Override
  public void timestamp(int usec, int type) {
    now = System.currentTimeMillis();
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // ---
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> getPutEvent() {
    if (System.currentTimeMillis() < now + WATCHDOG)
      return Optional.of(RimoPutEvent.STOP);
    return Optional.empty();
  }
}
