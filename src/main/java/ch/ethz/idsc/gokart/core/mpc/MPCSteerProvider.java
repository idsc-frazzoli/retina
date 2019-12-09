// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.HighPowerSteerPid;
import ch.ethz.idsc.gokart.calib.steer.SteerFeedForward;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ final class MPCSteerProvider extends MPCBaseProvider<SteerPutEvent> {
  // TODO JPH not too good location for vlp16 slowing
  private final Vlp16PassiveSlowing vlp16PassiveSlowing = ModuleAuto.INSTANCE.getInstance(Vlp16PassiveSlowing.class);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerPositionControl steerPositionController = new SteerPositionControl(HighPowerSteerPid.GLOBAL);
  private final MPCSteering mpcSteering;
  private boolean TorqueMode = false;

  public MPCSteerProvider(Timing timing, MPCSteering mpcSteering) {
    super(timing);
    this.mpcSteering = mpcSteering;
  }

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    // this safety bypass can be somewhere in a hi-frequency loop that is not related to rimo
    if (Objects.nonNull(vlp16PassiveSlowing))
      vlp16PassiveSlowing.bypassSafety();
    // ---
    Scalar time = Quantity.of(timing.seconds(), SI.SECOND);
    
    if (TorqueMode) {//Use Steering Torque
      Optional<Tensor> optional = mpcSteering.getSteeringTorque(time);
      System.out.println("Using T-Mode :) ");
      if (optional.isPresent()) {
        Tensor torqueMSG = optional.get();
        Scalar torqueCmd = torqueMSG.Get(0);
        System.out.println(torqueCmd);
        return Optional.of(SteerPutEvent.createOn(torqueCmd.multiply(MPCLudicConfig.GLOBAL.torqueScale)));
      }
    } else {//Use Steering Angle
      Optional<Tensor> optional = mpcSteering.getSteering(time);
      if (optional.isPresent()) {
        Tensor steering = optional.get();
        Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
        Scalar torqueCmd = steerPositionController.iterate( //
            currAngle, //
            steering.Get(0), //
            steering.Get(1));
        Scalar feedForward = SteerFeedForward.FUNCTION.apply(currAngle);
        return Optional.of(SteerPutEvent.createOn(torqueCmd.add(feedForward)));
      }
    }
    return Optional.empty();
  }

  /** Change the steering mode of the go-kart
   * 
   * @param useTorque (set True if go-kart should use the commanded torque instead of beta)
   * @return void */
  public void setSteeringMode(boolean useTorque) {
    this.TorqueMode = useTorque;
  }
}
