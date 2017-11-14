// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class HmiFullControlJoystick extends HmiAbstractJoystick {
  @Override
  protected double breakStrength() {
    return _joystick.getLeftKnobDirectionDown();
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    int sign = 1;

    @Override
    public Optional<RimoPutEvent> putEvent() {
      if (hasJoystick()) {
        GokartJoystickInterface joystick = _joystick;
        if (joystick.isButtonPressedBack())
          sign = -1;
        if (joystick.isButtonPressedStart())
          sign = +1;
        Scalar wheelL = RealScalar.of(joystick.getLeftSliderUnitValue() * sign);
        Scalar wheelR = RealScalar.of(joystick.getRightSliderUnitValue() * sign);
        return rimoRateControllerWrap.iterate( //
            getSpeedLimit().multiply(wheelL), //
            getSpeedLimit().multiply(wheelR));
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };

  @Override
  public RimoPutProvider getRimoPutProvider() {
    return rimoPutProvider;
  }
}
