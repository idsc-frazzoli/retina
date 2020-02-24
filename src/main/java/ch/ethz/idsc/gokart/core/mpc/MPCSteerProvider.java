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
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ final class MPCSteerProvider extends MPCBaseProvider<SteerPutEvent> {
  private static final Clip ANGLE_RANGE = Clips.interval(Quantity.of(-0.5, "SCE"), Quantity.of(0.5, "SCE"));
  // ---
  private final Vlp16PassiveSlowing vlp16PassiveSlowing = ModuleAuto.INSTANCE.getInstance(Vlp16PassiveSlowing.class);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerPositionControl steerPositionController = new SteerPositionControl(HighPowerSteerPid.GLOBAL);
  private final MPCSteering mpcSteering;
  private final boolean torqueMode;
  private final static Scalar MAX_DIFF = Quantity.of(0.05, "SCE");
  private int count = 0;

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
      return mpcSteering.getState(time).map(this::torqueSteer); // use steering torque
    else
      return mpcSteering.getSteering(time).map(this::angleSteer); // use steering angle
  }

  private SteerPutEvent torqueSteer(Tensor torqueMSG) {
    Scalar torqueCmd = torqueMSG.Get(0);
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar feedForward = SteerFeedForward.FUNCTION.apply(currAngle);
    this.count = this.count + 1;
    if (this.count >= MPCLudicConfig.GLOBAL.ledUpdateCycle) {
      // System.out.println("LED message triggered, count = " + this.count);
      // MPCSteerProvider.notifyLED(torqueMSG.Get(3), currAngle);
      this.count = 0;
      // System.out.println("LED message sent, count = " + this.count);
    }
    if (MPCLudicConfig.GLOBAL.powerSteer) {
      System.out.println("Torque msg: " + torqueCmd + ", Pwr Steer: " + feedForward);
      return SteerPutEvent.createOn(torqueCmd.add(feedForward).multiply(MPCLudicConfig.GLOBAL.torqueScale));
    }
    System.out.println("Torque msg: " + torqueCmd + ", Pwr Steer: off");
    return SteerPutEvent.createOn(torqueCmd.multiply(MPCLudicConfig.GLOBAL.torqueScale));
  }

  private SteerPutEvent angleSteer(Tensor steering) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar feedForward = SteerFeedForward.FUNCTION.apply(currAngle);
    this.count = this.count + 1;
    if (this.count >= MPCLudicConfig.GLOBAL.ledUpdateCycle) {
      // System.out.println("LED message triggered, count = " + this.count);
      MPCSteerProvider.notifyLED(steering.Get(0), currAngle);
      this.count = 0;
      // System.out.println("LED message sent, count = " + this.count);
    }
    if (MPCLudicConfig.GLOBAL.manualMode) {
      if (MPCLudicConfig.GLOBAL.powerSteer) {
        return SteerPutEvent.createOn(feedForward);
      } else {
        return SteerPutEvent.createOn(Quantity.of(0, "SCT"));
      }
    }
    Scalar torqueCmd = steerPositionController.iterate( //
        currAngle, //
        steering.Get(0), //
        steering.Get(1));
    return SteerPutEvent.createOn(torqueCmd.add(feedForward));
  }

  private static void notifyLED(Scalar referenceAngle, Scalar currAngle) {
	Scalar diff = referenceAngle.subtract(currAngle);
	Scalar absDiff=diff.abs();
	Boolean signDiff = Sign.isPositiveOrZero(diff);
	if(Sign.isPositiveOrZero(absDiff.subtract(MAX_DIFF))){ 
		if(signDiff){
		    int refIdx = 0;//angleToIdx(referenceAngle);
		    int valIdx = 9;//angleToIdx(currAngle);
		    System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
		    try {
		      LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		} else {
		    int refIdx = 18;// angleToIdx(referenceAngle);
		    int valIdx = 0;//angleToIdx(currAngle);
		    System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
		    try {
		      LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		}
	} else {
		int refIdx = 14;// angleToIdx(referenceAngle);
	    int valIdx = 14;//angleToIdx(currAngle);
	    System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
	    try {
	      LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	/*TODO previous implementation, to be removed
    int refIdx = angleToIdx(referenceAngle);
    int valIdx = angleToIdx(currAngle);
    System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
    try {
      LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
    } catch (Exception e) {
      e.printStackTrace();
    }
    */
  }

  private static int angleToIdx(Scalar angle) {
    double angleCorr = ANGLE_RANGE.apply(angle).number().doubleValue();
    return (int) Math.round((0.5 - angleCorr) * (LEDStatus.NUM_LEDS - 1));
  }
}
