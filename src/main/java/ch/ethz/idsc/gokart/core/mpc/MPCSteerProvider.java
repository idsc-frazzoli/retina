// code by mh, ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.HighPowerSteerPid;
import ch.ethz.idsc.gokart.calib.steer.SteerFeedForward;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ final class MPCSteerProvider extends MPCBaseProvider<SteerPutEvent> {
  private static final Clip ANGLE_RANGE = Clips.interval(-0.5, 0.5);
  // ---
  private final Vlp16PassiveSlowing vlp16PassiveSlowing = ModuleAuto.INSTANCE.getInstance(Vlp16PassiveSlowing.class);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerPositionControl steerPositionController = new SteerPositionControl(HighPowerSteerPid.GLOBAL);
  private final MPCSteering mpcSteering;
  private final boolean torqueMode;

  public MPCSteerProvider(Timing timing, MPCSteering mpcSteering, boolean torqueMode) {
    super(timing);
    this.mpcSteering = mpcSteering;
    this.torqueMode = torqueMode;
  }

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    // this safety bypass can be somewhere in a hi-frequency loop that is not related to rimo
    if (Objects.nonNull(vlp16PassiveSlowing))
      vlp16PassiveSlowing.bypassSafety();
    // ---
    Scalar time = Quantity.of(timing.seconds(), SI.SECOND);
    if (torqueMode)
      return mpcSteering.getSteeringTorque(time).map(this::torqueSteer); // use steering torque
    else
      return mpcSteering.getSteering(time).map(this::angleSteer); // use steering angle
  }

  private SteerPutEvent torqueSteer(Tensor torqueMSG) {
    Scalar torqueCmd = torqueMSG.Get(0);
    System.out.println(torqueCmd.multiply(MPCLudicConfig.GLOBAL.torqueScale)); // TODO remove after debugging
    return SteerPutEvent.createOn(torqueCmd.multiply(MPCLudicConfig.GLOBAL.torqueScale));
  }

  private SteerPutEvent angleSteer(Tensor steering) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    System.out.print("Beta: " + steering.Get(0)); // TODO remove after debugging
    System.out.println(" Dot Beta: " + steering.Get(1)); // TODO remove after debugging
    Scalar torqueCmd = steerPositionController.iterate( //
        currAngle, //
        steering.Get(0), //
        steering.Get(1));
    Scalar feedForward = SteerFeedForward.FUNCTION.apply(currAngle);
    System.out.println(torqueCmd.add(feedForward)); // TODO remove after debugging
    MPCSteerProvider.notifyLED(steering.Get(0), currAngle); // either directly query config or always publish but only listen when desired
    // Scalar negator = torqueCmd.add(feedForward).negate();
    return SteerPutEvent.createOn(torqueCmd.add(feedForward) /* .add(negator) */ );
  }

  private static void notifyLED(Scalar referenceAngle, Scalar currAngle) {
    int refIdx = angleToIdx(referenceAngle);
    int valIdx = angleToIdx(currAngle);
    try {
      LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static int angleToIdx(Scalar angle) {
    double angleCorr = ANGLE_RANGE.apply(angle).number().doubleValue();
    return (int) Math.round(0.5 - angleCorr) * (LEDStatus.NUM_LEDS - 1);
  }
}
