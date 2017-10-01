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
  // ---
  private boolean flag = false;
  // TODO NRJ detect anomaly on brake... perhaps put in different module?

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(flag ? RimoPutEvent.STOP : null);
  }

  @Override
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    flag |= !linmotGetEvent.isSafeWindingTemperature1();
    flag |= !linmotGetEvent.isSafeWindingTemperature2();
  }
}
