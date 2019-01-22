// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.HighPowerSteerConfig;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ final class MPCSteerProvider extends MPCBaseProvider<SteerPutEvent> {
  // TODO JPH not too good location for vlp16 slowing
  private final Vlp16PassiveSlowing vlp16PassiveSlowing = ModuleAuto.INSTANCE.getInstance(Vlp16PassiveSlowing.class);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerPositionControl steerPositionController = new SteerPositionControl(HighPowerSteerConfig.GLOBAL);
  private final MPCSteering mpcSteering;

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
    Scalar steering = mpcSteering.getSteering(time);
    Scalar dSteering = mpcSteering.getDotSteering(time);
    if (Objects.nonNull(steering)) {
      Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
      Scalar torqueCmd = steerPositionController.iterate(currAngle, steering, dSteering);
      return Optional.of(SteerPutEvent.createOn(torqueCmd));
    }
    return Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
  }
}
