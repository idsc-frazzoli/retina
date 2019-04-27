// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** prevents driving if pose is has insufficient quality for timeout duration */
public class LocalizationEmergencyModule extends AbstractModule implements GokartPoseListener, RimoPutProvider {
  /** timeout 1[s] */
  private final Watchdog watchdog = SoftWatchdog.barking(1.0);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();

  @Override // from AbstractModule
  protected void first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent.getQuality()))
      watchdog.notifyWatchdog();
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return watchdog.isBarking() //
        ? Optional.of(RimoPutEvent.PASSIVE)
        : Optional.empty();
  }
}
