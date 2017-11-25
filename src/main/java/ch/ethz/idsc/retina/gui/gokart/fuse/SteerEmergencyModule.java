// code by jph
package ch.ethz.idsc.retina.gui.gokart.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** sends stop command if steer angle is not calibrated or tracking is unhealthy */
public class SteerEmergencyModule extends AbstractModule implements RimoPutProvider {
  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    boolean isCalibratedAndHealthy = //
        SteerSocket.INSTANCE.getSteerColumnTracker().isCalibratedAndHealthy();
    return Optional.ofNullable(isCalibratedAndHealthy ? null : RimoPutEvent.STOP);
  }
}
