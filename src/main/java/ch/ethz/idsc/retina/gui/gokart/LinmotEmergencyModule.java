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

/** sends stop command if either winding temperature is outside valid range
 * 
 * module needs to be started after linmot calibration procedure */
public class LinmotEmergencyModule extends AbstractModule implements LinmotGetListener, RimoPutProvider {
  private boolean isEmergency = false;

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

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(isEmergency ? RimoPutEvent.STOP : null);
  }

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    isEmergency |= !linmotGetEvent.isOperational();
    isEmergency |= !linmotGetEvent.isSafeWindingTemperature1();
    isEmergency |= !linmotGetEvent.isSafeWindingTemperature2();
  }
}
