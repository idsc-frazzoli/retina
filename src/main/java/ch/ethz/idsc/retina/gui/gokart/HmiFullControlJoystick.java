// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

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
        GenericXboxPadJoystick joystick = _joystick;
        if (joystick.isButtonPressedBack())
          sign = -1;
        if (joystick.isButtonPressedStart())
          sign = 1;
        double wheelL = joystick.getLeftSliderUnitValue();
        double wheelR = joystick.getRightSliderUnitValue();
        return Optional.of(RimoPutEvent.withSpeeds( //
            (short) (wheelL * getSpeedLimit() * sign), //
            (short) (wheelR * getSpeedLimit() * sign)));
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
