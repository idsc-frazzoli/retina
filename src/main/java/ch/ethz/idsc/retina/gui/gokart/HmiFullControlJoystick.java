// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class HmiFullControlJoystick extends HmiAbstractJoystick {
  @Override
  protected double breakStrength(GokartJoystickInterface gokartJoystickInterface) {
    return gokartJoystickInterface.getBreakStrength();
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    int sign = 1;

    @Override
    public Optional<RimoPutEvent> putEvent() {
      Optional<GokartJoystickInterface> optional = getJoystick();
      if (optional.isPresent()) {
        GokartJoystickInterface joystick = optional.get();
        Optional<Integer> speed = joystick.getSpeedMultiplierOptional();
        if (speed.isPresent())
          sign = speed.get();
        Scalar wheelL = RealScalar.of(joystick.getAheadTireLeft_Unit() * sign);
        Scalar wheelR = RealScalar.of(joystick.getAheadTireRight_Unit() * sign);
        return rimoRateControllerWrap.iterate(Tensors.of( //
            getSpeedLimit().multiply(wheelL), //
            getSpeedLimit().multiply(wheelR)));
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
