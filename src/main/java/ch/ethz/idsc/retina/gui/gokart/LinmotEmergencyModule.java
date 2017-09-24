// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** sends stop command if either winding temperature is outside valid range */
public class LinmotEmergencyModule extends AbstractModule implements LinmotGetListener, RimoPutProvider {
  /** degree celsius */
  private static final double MIN_C = 2;
  // TODO NRJ check valid range
  private static final double MAX_C = 110;
  // ---
  private boolean flag = false;

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addListener(this);
    RimoSocket.INSTANCE.addProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeProvider(this);
    LinmotSocket.INSTANCE.removeListener(this);
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
  public void digest(LinmotGetEvent linmotGetEvent) {
    // too cold
    flag |= linmotGetEvent.windingTemperature1() < MIN_C;
    flag |= linmotGetEvent.windingTemperature2() < MIN_C;
    // too hot
    flag |= MAX_C < linmotGetEvent.windingTemperature1();
    flag |= MAX_C < linmotGetEvent.windingTemperature2();
  }
}
