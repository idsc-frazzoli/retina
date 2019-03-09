// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringClip;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;

/** class was designed to exaggerate rotation of gokart */
public class DriftThrustManualModule extends GuideManualModule<RimoPutEvent> {
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
    Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ(); // unit s^-1
    // ahead value may be negative
    Scalar ahead = Differences.of(manualControlInterface.getAheadPair_Unit()).Get(0);
    Scalar delta = gyroZ.multiply(ManualConfig.GLOBAL.torquePerGyro);
    Tensor power = TorqueVectoringClip.of(ahead.add(delta), ahead.subtract(delta)) //
        .multiply(ManualConfig.GLOBAL.torqueLimit);
    short arms_rawL = Magnitude.ARMS.toShort(power.Get(0));
    short arms_rawR = Magnitude.ARMS.toShort(power.Get(1));
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_rawL, // sign left invert
        (short) +arms_rawR // sign right id
    ));
  }
}
