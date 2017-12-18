// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

class PurePursuitRimo extends PurePursuitBase implements RimoPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerWrap();

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
      control(steerColumnInterface);
    return Optional.empty();
  }

  /* package */ Optional<RimoPutEvent> control(SteerColumnInterface steerColumnInterface) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar speed = PursuitConfig.GLOBAL.rateFollower;
      DifferentialSpeed differentialSpeed = ChassisGeometry.GLOBAL.getDifferentialSpeed();
      Scalar theta = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface);
      Tensor pair = differentialSpeed.pair(speed, theta);
      return rimoRateControllerWrap.iterate(pair);
    }
    return Optional.empty();
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
