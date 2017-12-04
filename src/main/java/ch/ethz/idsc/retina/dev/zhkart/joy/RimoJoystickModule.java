// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RimoJoystickModule extends AbstractModule implements RimoPutProvider {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerWrap();

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoRateControllerWrap);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoRateControllerWrap);
    joystickLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
    return optional.isPresent() //
        ? control(steerColumnInterface, (GokartJoystickInterface) optional.get())
        : Optional.empty();
  }

  public Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, //
      GokartJoystickInterface joystick) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar speed = RimoConfig.GLOBAL.rateLimit.multiply(joystick.getAheadAverage());
      DifferentialSpeed differentialSpeed = ChassisGeometry.GLOBAL.getDifferentialSpeed();
      Scalar theta = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface);
      Tensor pair = differentialSpeed.pair(speed, theta);
      Tensor bias = joystick.getAheadPair_Unit().multiply(RimoConfig.GLOBAL.rateLimit);
      return rimoRateControllerWrap.iterate(pair.add(bias));
    }
    return Optional.empty();
  }
}
