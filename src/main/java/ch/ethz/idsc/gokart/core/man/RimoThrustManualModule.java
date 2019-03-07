// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Differences;

public class RimoThrustManualModule extends GuideManualModule<RimoPutEvent> {
  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    Scalar pair = Differences.of(manualControlInterface.getAheadPair_Unit()).Get(0);
    // Scalar pair = joystick.getAheadPair_Unit().Get(1); // entry in [0, 1]
    pair = pair.multiply(ManualConfig.GLOBAL.torqueLimit);
    short arms_raw = Magnitude.ARMS.toShort(pair); // confirm that units are correct
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_raw, // sign left invert
        (short) +arms_raw // sign right id
    ));
  }
}
