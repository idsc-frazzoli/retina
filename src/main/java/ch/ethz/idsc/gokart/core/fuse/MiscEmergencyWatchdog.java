// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscGetListener;
import ch.ethz.idsc.gokart.dev.misc.MiscSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;

/** misc emergency refers to the state of the micro-autobox when
 * a comm initialization is missing or a comm failure was detected.
 * 
 * the module sends rimo passive command if the emergency flag is set in {@link MiscGetEvent} */
public final class MiscEmergencyWatchdog extends EmergencyModule<RimoPutEvent> implements MiscGetListener {
  private boolean isEmergency = true;

  @Override // from AbstractModule
  protected void first() throws Exception {
    MiscSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    MiscSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from MiscGetListener
  public void getEvent(MiscGetEvent miscGetEvent) {
    isEmergency = miscGetEvent.isEmergency();
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return isEmergency //
        ? StaticHelper.OPTIONAL_RIMO_PASSIVE
        : Optional.empty();
  }
}
