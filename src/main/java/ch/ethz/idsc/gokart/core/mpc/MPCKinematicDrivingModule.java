// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
//Not in use yet
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.fuse.SpeedLimitSafetyModule;
import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionRender;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCKinematicDrivingModule extends AbstractModule {
  public final LcmMPCControlClient lcmMPCPathFollowingClient//
      = new LcmMPCControlClient();
  private final MPCOptimizationConfig mpcPathFollowingConfig = MPCOptimizationConfig.GLOBAL;
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final MPCSteering mpcSteering = new MPCOpenLoopSteering();
  private final MPCBraking mpcBraking = new MPCSimpleBraking();
  private final MPCPower mpcPower;
  private final MPCStateEstimationProvider mpcStateEstimationProvider;
  private final SteerPositionControl steerPositionController = new SteerPositionControl();
  private final Stopwatch started;
  private final Timer timer = new Timer();
  private final int previewSize = MPCNative.SPLINEPREVIEWSIZE;
  private final MPCPreviewableTrack track;
  
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();

  /** switch to testing binary that send back test data has to be called before first */
  public void switchToTest() {
    lcmMPCPathFollowingClient.switchToTest();
  }

  /** create Module with custom estimator
   * 
   * @param estimator the custom estimator
   * @param started stopwatch that shows the same time that also was used for the custom estimator */
  public MPCKinematicDrivingModule(MPCStateEstimationProvider estimator, Stopwatch started, MPCPreviewableTrack track) {
    this.track = track;
    mpcStateEstimationProvider = estimator;
    this.started = started;
    // link mpc steering
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    initModules();
  }

  /** create Module with standard estimator */
  public MPCKinematicDrivingModule() {
    track = DubendorfTrack.HYPERLOOP_EIGHT;
    started = Stopwatch.started();
    mpcStateEstimationProvider = new SimpleKinematicMPCStateEstimationProvider(started);
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    initModules();
  }

  public void addPredictionRender(MPCPredictionRender mpcPredictionRender) {
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcPredictionRender);
  }

  private void initModules() {
    // link mpc steering
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcSteering);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcPower);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcBraking);
    // state estimation provider
    mpcBraking.setStateProvider(mpcStateEstimationProvider);
    mpcPower.setStateProvider(mpcStateEstimationProvider);
    mpcSteering.setStateProvider(mpcStateEstimationProvider);
  }

  public final PutProvider<RimoPutEvent> rimoProvider = new PutProvider<RimoPutEvent>() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      Scalar time = Quantity.of(started.display_seconds(), SI.SECOND);
      Tensor currents = mpcPower.getPower(time);
      if (Objects.nonNull(currents))
        return Optional.of(RimoPutHelper.operationTorque( //
            (short) Magnitude.ARMS.toFloat(currents.Get(0)), // sign left invert
            (short) Magnitude.ARMS.toFloat(currents.Get(1)) // sign right id
        ));
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.AUTONOMOUS;
    }
  };
  public final PutProvider<SteerPutEvent> steerProvider = new PutProvider<SteerPutEvent>() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      Scalar time = Quantity.of(started.display_seconds(), SI.SECOND);
      Scalar steering = mpcSteering.getSteering(time);
      if (Objects.nonNull(steering)) {
        Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
        Scalar difference = steering.subtract(currAngle);
        Scalar torqueCmd = steerPositionController.iterate(difference);
        return Optional.of(SteerPutEvent.createOn(torqueCmd));
      }
      return Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.AUTONOMOUS;
    }
  };
  public final PutProvider<LinmotPutEvent> linmotProvider = new PutProvider<LinmotPutEvent>() {
    @Override
    public Optional<LinmotPutEvent> putEvent() {
      Scalar time = Quantity.of(started.display_seconds(), SI.SECOND);
      Scalar braking = mpcBraking.getBraking(time);
      if (Objects.nonNull(braking)) {
        return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(braking));
      }
      // this should not happen
      return Optional.of(LinmotPutOperation.INSTANCE.fallback());
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.AUTONOMOUS;
    }
  };

  @Override
  protected void first() throws Exception {
    lcmMPCPathFollowingClient.start();
    mpcStateEstimationProvider.first();
    joystickLcmProvider.startSubscriptions();
    // start update cycle
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        // use joystick for speed limit
        //get joystick
        Scalar maxSpeed = Quantity.of(0, SI.VELOCITY);
         Optional<JoystickEvent> optionalJoystick = joystickLcmProvider.getJoystick();
         if (optionalJoystick.isPresent()) { // is joystick button "autonomous" pressed?
           GokartJoystickInterface actualJoystick = (GokartJoystickInterface) optionalJoystick.get();
           Scalar forward = actualJoystick.getAheadPair_Unit().Get(1);
           maxSpeed = mpcPathFollowingConfig.maxSpeed.multiply(forward);
         }
         
         //send message with max speed
         //optimization parameters will have more values in the future
         MPCOptimizationParameter mpcOptimizationParameter = new MPCOptimizationParameter(maxSpeed);
         lcmMPCPathFollowingClient.publishOptimizationParameter(mpcOptimizationParameter);
         
        // send the newest state and start the update state
        GokartState state = mpcStateEstimationProvider.getState();
        Tensor position = Tensors.of(state.getX(), state.getY());
        MPCPathParameter mpcPathParameter = track.getPathParameterPreview(previewSize, position);
        lcmMPCPathFollowingClient.publishControlRequest(state, mpcPathParameter);
      }
    }, (long) (mpcPathFollowingConfig.updateCycle.number().floatValue() * 1000));
    ModuleAuto.INSTANCE.runOne(SpeedLimitSafetyModule.class);
  }

  @Override
  protected void last() {
    lcmMPCPathFollowingClient.stop();
    mpcStateEstimationProvider.last();
    joystickLcmProvider.stopSubscriptions();
    timer.cancel();
    ModuleAuto.INSTANCE.terminateOne(SpeedLimitSafetyModule.class);
  }
}
