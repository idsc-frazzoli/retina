// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RimoTorqueManualModule extends GuideManualModule<RimoPutEvent> {
  private static final Scalar HALF = DoubleScalar.of(0.5);

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
    Scalar factor = manualControlInterface.getAheadAverage(); // [-1, 1]
    Tensor pair = manualControlInterface.getAheadPair_Unit(); // entries both in [0, 1]
    pair = pair.map(s -> s.add(factor)).multiply(HALF).multiply(ManualConfig.GLOBAL.torqueLimit);
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -Magnitude.ARMS.toShort(pair.Get(0)), // sign left invert
        (short) +Magnitude.ARMS.toShort(pair.Get(1)) // sign right id
    ));
  }
}
