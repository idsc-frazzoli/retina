// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public class LocalizationEmergencyModule extends AbstractModule implements RimoPutProvider, GokartPoseListener {
  private final Watchdog watchdog = SoftWatchdog.barking(1);

  @Override
  protected void first() {
    // TODO JPH Auto-generated method stub
  }

  @Override
  protected void last() {
    // TODO JPH Auto-generated method stub
  }

  @Override
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent.getQuality()))
      watchdog.notifyWatchdog();
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return watchdog.isBarking() //
        ? Optional.of(RimoPutEvent.PASSIVE)
        : Optional.empty();
  }
}
