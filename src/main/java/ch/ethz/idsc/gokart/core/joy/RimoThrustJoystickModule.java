// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Differences;

public class RimoThrustJoystickModule extends GuideJoystickModule<RimoPutEvent> {
  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar pair = Differences.of(joystick.getAheadPair_Unit()).Get(0);
    // Scalar pair = joystick.getAheadPair_Unit().Get(1); // entry in [0, 1]
    pair = pair.multiply(JoystickConfig.GLOBAL.torqueLimit);
    pair = RimoPutTire.MAGNITUDE_ARMS.apply(pair); // confim that units are correct
    short armsL_raw = (short) (-pair.number().shortValue()); // sign left invert
    short armsR_raw = (short) (+pair.number().shortValue()); // sign right id
    return Optional.of(RimoPutHelper.operationTorque(armsL_raw, armsR_raw));
  }
}
