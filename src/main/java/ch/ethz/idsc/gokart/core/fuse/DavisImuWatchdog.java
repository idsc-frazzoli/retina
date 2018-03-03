// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** TODO purpose */
public class DavisImuWatchdog extends AbstractModule implements RimoPutProvider, DavisImuFrameListener {
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private Watchdog watchdog; // 150[ms]

  public DavisImuWatchdog() {
    davisImuLcmClient.addListener(this);
  }

  @Override
  protected void first() throws Exception {
    watchdog = new Watchdog(0.15); // 150[ms]
    davisImuLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    davisImuLcmClient.stopSubscriptions();
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    boolean isBlown = Objects.nonNull(watchdog) && watchdog.isBlown(); // true == stop gokart
    return Optional.ofNullable(isBlown ? RimoPutEvent.PASSIVE : null);
  }

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    watchdog.pacify();
  }
}
