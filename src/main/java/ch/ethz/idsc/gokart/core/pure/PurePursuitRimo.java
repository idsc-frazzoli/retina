// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.ProviderRank;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerDuo;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;

class PurePursuitRimo extends PurePursuitBase implements RimoPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  /** available implementations of RimoRateControllerWrap are
   * {@link RimoRateControllerUno}, and {@link RimoRateControllerDuo} */
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = //
      new RimoRateControllerUno(); // <- UNO uses a single PI-controller

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
