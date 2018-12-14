// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.LabjackAdcLcmClient;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;

public class RimoThrustThrottleModule extends GuideJoystickModule<RimoPutEvent> {
  private final LabjackAdcLcmClient labjackAdcLcmClient = new LabjackAdcLcmClient();

  @Override // from AbstractModule
  void protected_first() {
    labjackAdcLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    labjackAdcLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar pair = labjackAdcLcmClient.getAheadSigned();
    // Differences.of(joystick.getAheadPair_Unit()).Get(0);
    // Scalar pair = joystick.getAheadPair_Unit().Get(1); // entry in [0, 1]
    pair = pair.multiply(JoystickConfig.GLOBAL.torqueLimit);
    short arms_raw = Magnitude.ARMS.toShort(pair); // confirm that units are correct
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_raw, // sign left invert
        (short) +arms_raw // sign right id
    ));
  }
}
