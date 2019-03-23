// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

/** module ensures that VMU931 has carried out calibration procedure since startup of sensor
 * otherwise operation of the gokart motors is suppressed */
public class Vmu931CalibrationWatchdog extends EmergencyModule<RimoPutEvent> {
  private final Vmu931LcmServerModule vmu931LcmServerModule = //
      ModuleAuto.INSTANCE.getInstance(Vmu931LcmServerModule.class);

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
    return vmu931LcmServerModule.isCalibrated() //
        ? Optional.empty()
        : StaticHelper.OPTIONAL_RIMO_PASSIVE;
  }
}
