// code by mh, ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.HighPowerSteerPid;
import ch.ethz.idsc.gokart.calib.steer.SteerFeedForward;
import ch.ethz.idsc.gokart.core.adas.HapticSteerConfig;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;

/* package */ final class MPCSteerProvider extends MPCBaseProvider<SteerPutEvent> {
  private static final Scalar ZERO_ADDITION = Quantity.of(0, SteerPutEvent.UNIT_RTORQUE);
  private final Vlp16PassiveSlowing vlp16PassiveSlowing = ModuleAuto.INSTANCE.getInstance(Vlp16PassiveSlowing.class);
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerPositionControl steerPositionController = new SteerPositionControl(HighPowerSteerPid.GLOBAL);
  private final MPCSteering mpcSteering;
  private final boolean torqueMode;
  private final boolean powerSteerMode;
  private final GeodesicIIR1 velocityGeodesicIIR1;
  private final HapticSteerConfig hapticSteerConfig = HapticSteerConfig.GLOBAL;
  private Scalar powerSteerAddition = ZERO_ADDITION;

  public MPCSteerProvider(Timing timing, MPCSteering mpcSteering, boolean torqueMode, boolean powerSteerMode) {
    super(timing);
    this.mpcSteering = mpcSteering;
    this.torqueMode = torqueMode;
    this.powerSteerMode = powerSteerMode;
    velocityGeodesicIIR1 = new GeodesicIIR1(RnGeodesic.INSTANCE, hapticSteerConfig.velocityFilter);
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
    powerSteer().ifPresent(this::pwrSetter); // add the power steer component
    System.out.println(powerSteerAddition);// TODO remove after debugging
    return SteerPutEvent.createOn(torqueCmd.multiply(MPCLudicConfig.GLOBAL.torqueScale).add(powerSteerAddition));
  }

  private SteerPutEvent angleSteer(Tensor steering) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar torqueCmd = steerPositionController.iterate( //
        currAngle, //
        steering.Get(0), //
        steering.Get(1));
    Scalar feedForward = SteerFeedForward.FUNCTION.apply(currAngle);
    System.out.println(torqueCmd.add(feedForward)); // TODO remove after debugging
    powerSteer().ifPresent(this::pwrSetter); // add the power steer component
    System.out.println(powerSteerAddition);// TODO remove after debugging
    return SteerPutEvent.createOn(torqueCmd.add(feedForward).add(powerSteerAddition));
  }

  private Optional<Scalar> powerSteer() {
    Scalar time = Quantity.of(timing.seconds(), SI.SECOND);
    return mpcSteering.getState(time).map(this::apply);
  }

  private Scalar apply(Tensor state) {
    Scalar feedForwardValue = SteerFeedForward.FUNCTION.apply(state.Get(5));
    Scalar term0 = hapticSteerConfig.feedForward //
        ? feedForwardValue //
        : feedForwardValue.zero();
    Scalar term1 = term1(state.Get(5), //
        Tensors.of(state.Get(0), state.Get(1)));
    return term0.add(term1);
  }

  private Scalar term1(Scalar currangle, Tensor velocity) {
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(currangle);
    Tensor filteredVel = velocityGeodesicIIR1.apply(velocity);
    Scalar latFront_LeftVel = axleConfiguration.wheel(0).adjoint(filteredVel).Get(1);
    Scalar latFrontRightVel = axleConfiguration.wheel(1).adjoint(filteredVel).Get(1);
    return hapticSteerConfig.latForceCompensationBoundaryClip().apply( //
        latFront_LeftVel.add(latFrontRightVel).multiply(hapticSteerConfig.latForceCompensation));
  }

  private void pwrSetter(Scalar amount) {
    if (powerSteerMode)
      powerSteerAddition = amount;
    else
      powerSteerAddition = ZERO_ADDITION;
  }
}
