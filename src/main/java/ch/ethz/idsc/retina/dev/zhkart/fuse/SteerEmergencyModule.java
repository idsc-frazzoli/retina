// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** sends stop command as soon as steer angle is not calibrated or steer angle tracking is unhealthy */
public final class SteerEmergencyModule extends AbstractModule implements RimoPutProvider {
  private boolean isBlown = false;

  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    isBlown |= !SteerSocket.INSTANCE.getSteerColumnTracker().isCalibratedAndHealthy();
    return Optional.ofNullable(isBlown ? RimoPutEvent.STOP : null); // deactivate throttle
  }
}
