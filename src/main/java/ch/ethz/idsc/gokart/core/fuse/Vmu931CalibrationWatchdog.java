// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

/** module ensures that VMU931 has carried out calibration procedure since startup of sensor
 * otherwise operation of the gokart motors is suppressed */
public final class Vmu931CalibrationWatchdog extends EmergencyModule<RimoPutEvent> {
  @Override // from AbstractModule
  protected void first() {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    return isCalibrated() //
        ? Optional.empty()
        : RimoPutEvent.OPTIONAL_RIMO_PASSIVE;
  }

  private static boolean isCalibrated() {
    Vmu931LcmServerModule vmu931LcmServerModule = //
        ModuleAuto.INSTANCE.getInstance(Vmu931LcmServerModule.class);
    return Objects.nonNull(vmu931LcmServerModule) //
        && vmu931LcmServerModule.isCalibrated();
  }
}
