// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** sends stop command if steer battery voltage is below threshold
 * or if emergency flag is set in {@link MiscGetEvent} */
public class MiscEmergencyModule extends AbstractModule implements MiscGetListener, RimoPutProvider {
  private static final double THRESHOLD_V = 10.7;
  // ---
  private boolean flag = false;

  @Override
  protected void first() throws Exception {
    MiscSocket.INSTANCE.addListener(this);
    RimoSocket.INSTANCE.addProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeProvider(this);
    MiscSocket.INSTANCE.removeListener(this);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> getPutEvent() {
    return Optional.ofNullable(flag ? RimoPutEvent.STOP : null);
  }

  @Override
  public void digest(MiscGetEvent miscGetEvent) {
    flag |= miscGetEvent.steerBatteryVoltage() < THRESHOLD_V;
    flag |= miscGetEvent.isEmergency();
  }
}
