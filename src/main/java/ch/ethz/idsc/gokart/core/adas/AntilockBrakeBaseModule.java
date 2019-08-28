// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;

public abstract class AntilockBrakeBaseModule extends AbstractModule implements LinmotPutProvider {
  protected final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  protected final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();
  // ---
  protected RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  protected final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // ---
  protected final HapticSteerConfig hapticSteerConfig;
  /** velocity is higher than setVel -> full stop */
  protected Scalar brakePosition;

  public AntilockBrakeBaseModule(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
    brakePosition = hapticSteerConfig.fullBraking;
  }

  @Override // from LinmotPutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }
}
