// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class RimoJoystickModule extends AbstractModule implements RimoPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerWrap();

  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    Optional<GokartJoystickInterface> optional = null; // FIXME
    if (optional.isPresent() && steerColumnTracker.isCalibrated()) {
      // TODO add tire bias
      Scalar speed = RimoConfig.GLOBAL.rateLimit.multiply(RealScalar.of(optional.get().getAheadAverage()));
      Scalar axisDelta = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
      Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRearMeter();
      DifferentialSpeed differentialSpeed = new DifferentialSpeed(axisDelta, yTireRear);
      Scalar theta = SteerConfig.getAngleFromSCE(steerColumnTracker.getEncoderValueCentered());
      return rimoRateControllerWrap.iterate(differentialSpeed.pair(speed, theta));
    }
    return Optional.empty();
  }
}
