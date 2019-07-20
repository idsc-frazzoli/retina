// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringClip;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Ramp;

/** class was designed to exaggerate rotation of gokart
 * 
 * https://www.youtube.com/watch?v=zcBImlS0sE4 */
public class DriftThrustManualModule extends GuideManualModule<RimoPutEvent> implements GokartPoseListener {
  private static final Clip DELTA_CLIP = Clips.absoluteOne();
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override // from AbstractModule
  void protected_first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    gokartPoseLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, //
      ManualControlInterface manualControlInterface) {
    return Optional.of(derive( //
        Differences.of(manualControlInterface.getAheadPair_Unit()).Get(0), //
        gokartPoseEvent.getGyroZ(), //
        DriftRatio.of(gokartPoseEvent.getVelocity())));
  }

  /** @param ahead in the interval [-1, 1]
   * @param gyroZ with unit s^-1
   * @param driftRatio unitless
   * @return */
  /* package */ RimoPutEvent derive(Scalar ahead, Scalar gyroZ, Scalar driftRatio) {
    Scalar delta = DELTA_CLIP.of(gyroZ.multiply(ManualConfig.GLOBAL.torquePerGyro));
    Scalar overDrift = Ramp.of(driftRatio.abs().subtract(ManualConfig.GLOBAL.driftAvoidStart));
    Scalar driftfactor = Ramp.of(RealScalar.ONE.subtract(overDrift.multiply(ManualConfig.GLOBAL.driftAvoidRamp)));
    delta = driftfactor.multiply(delta);
    Tensor power = TorqueVectoringClip.from(ahead, delta.negate()).multiply(ManualConfig.GLOBAL.torqueLimit);
    short arms_rawL = Magnitude.ARMS.toShort(power.Get(0));
    short arms_rawR = Magnitude.ARMS.toShort(power.Get(1));
    return RimoPutHelper.operationTorque( //
        (short) -arms_rawL, // sign left invert
        (short) +arms_rawR // sign right id
    );
  }
}
