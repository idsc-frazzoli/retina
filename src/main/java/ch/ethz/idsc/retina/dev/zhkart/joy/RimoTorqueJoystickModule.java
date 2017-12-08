// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RimoTorqueJoystickModule extends JoystickModule<RimoPutEvent> {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();

  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from JoystickModule
  Optional<RimoPutEvent> translate(GokartJoystickInterface joystick) {
    return control(steerColumnInterface, joystick);
  }

  /** @param steerColumnInterface
   * @param joystick
   * @return */
  /* package */ Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, //
      GokartJoystickInterface joystick) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar torque = RimoConfig.GLOBAL.torqueLimit; // ARMS
      Scalar factor = joystick.getAheadAverage();
      Tensor pair = joystick.getAheadPair_Unit();
      pair = pair.map(s -> s.add(factor)).multiply(RealScalar.of(0.5)).multiply(torque);
      short armsL_raw = (short) (-pair.Get(0).number().shortValue());
      short armsR_raw = (short) (+pair.Get(1).number().shortValue());
      return Optional.of(new RimoPutEvent( //
          new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
          new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
      ));
    }
    return Optional.empty();
  }
}
