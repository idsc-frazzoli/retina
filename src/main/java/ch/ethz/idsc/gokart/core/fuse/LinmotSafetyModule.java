// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** sends stop command if linmot status is "not-operational"
 * {@link LinmotGetEvent#isOperational()}
 * 
 * <p>The module has {@link ProviderRank#SAFETY} to prevent the autonomous
 * drive without operational brake. */
public final class LinmotSafetyModule extends AbstractModule implements LinmotGetListener, PutProvider<RimoPutEvent> {
  private boolean isOperational = false;

  @Override // from AbstractModule
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    LinmotSocket.INSTANCE.removeGetListener(this);
  }

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    isOperational = linmotGetEvent.isOperational();
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.SAFETY;
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    return isOperational //
        ? Optional.empty()
        : StaticHelper.OPTIONAL_RIMO_PASSIVE;
  }
}
