// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.slam.DriftRatio;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringClip;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Ramp;

/** class was designed to exaggerate rotation of gokart
 * outcome of the experiment:
 * https://www.youtube.com/watch?v=zcBImlS0sE4 */
public class DriftThrustManualModule extends GuideManualModule<RimoPutEvent> {
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);

  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  private static final Clip deltaClip = Clips.absoluteOne();

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    Scalar gyroZ = lidarLocalizationModule.getGyroZFiltered();
    // ahead value may be negative
    Scalar ahead = Differences.of(manualControlInterface.getAheadPair_Unit()).Get(0);
    Scalar delta = deltaClip.of(gyroZ.multiply(ManualConfig.GLOBAL.torquePerGyro));
    if (Objects.isNull(lidarLocalizationModule))
      return Optional.empty();
    Scalar drift = DriftRatio.of(lidarLocalizationModule.getVelocity());
    Scalar overDrift = Ramp.of(Abs.of(drift).subtract(ManualConfig.GLOBAL.driftAvoidStart));
    Scalar driftfactor = Ramp.of(RealScalar.ONE.subtract(overDrift.multiply(ManualConfig.GLOBAL.driftAvoidRamp)));
    delta = driftfactor.multiply(delta);
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
