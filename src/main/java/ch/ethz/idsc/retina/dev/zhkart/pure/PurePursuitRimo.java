// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerDuo;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

class PurePursuitRimo extends PurePursuitBase implements RimoPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = //
      new RimoRateControllerDuo();

  @Override // from StartAndStoppable
  public void start() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoRateControllerWrap);
  }

  @Override // from StartAndStoppable
  public void stop() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoRateControllerWrap);
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    if (isOperational())
      return control(steerColumnInterface);
    return Optional.empty();
  }

  /* package */ Optional<RimoPutEvent> control(SteerColumnInterface steerColumnInterface) {
    if (steerColumnInterface.isSteerColumnCalibrated())
      return rimoRateControllerWrap.iterate( //
          PursuitConfig.GLOBAL.rateFollower, // average target velocity
          SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface)); // steering angle
    return Optional.empty();
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
