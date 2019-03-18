// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;

public class Vmu931CalibrationWatchdog extends EmergencyModule<RimoPutEvent> {
  // TODO JPH not good style
  public static boolean requiresCalibration = false;

  @Override // from AbstractModule
  protected void first() {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return requiresCalibration //
        ? StaticHelper.OPTIONAL_RIMO_PASSIVE
        : Optional.empty();
  }
}
