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
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RimoJoystickModule extends AbstractModule implements RimoPutProvider {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerWrap();

  @Override
  protected void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    joystickLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
    if (optional.isPresent() && steerColumnInterface.isSteerColumnCalibrated()) {
      GokartJoystickInterface joystick = (GokartJoystickInterface) optional.get();
      Scalar speed = RimoConfig.GLOBAL.rateLimit.multiply(RealScalar.of(joystick.getAheadAverage()));
      Scalar axisDelta = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
      Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRearMeter();
      DifferentialSpeed differentialSpeed = new DifferentialSpeed(axisDelta, yTireRear);
      Scalar theta = SteerConfig.getAngleFromSCE(steerColumnInterface);
      Tensor pair = differentialSpeed.pair(speed, theta);
      Tensor bias = joystick.getAheadPair_Unit().multiply(RimoConfig.GLOBAL.rateLimit);
      return rimoRateControllerWrap.iterate(pair.add(bias));
    }
    return Optional.empty();
  }
}
