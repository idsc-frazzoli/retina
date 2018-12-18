// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
//Not in use yet
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.gokart.core.map.GokartTrackIdentificationModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

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
  private Timer timer = new Timer();
  private final int previewSize = MPCNative.SPLINEPREVIEWSIZE;
  private final MPCPreviewableTrack track;
  private final ManualControlProvider joystickLcmProvider = ManualConfig.GLOBAL.createProvider();
  private TimerTask controlRequestTask;

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

  /** create Module with custom estimator and with life track
   * 
   * @param estimator the custom estimator
   * @param started stopwatch that shows the same time that also was used for the custom estimator */
  public MPCKinematicDrivingModule(MPCStateEstimationProvider estimator, Stopwatch started) {
    this.track = null;
    mpcStateEstimationProvider = estimator;
    this.started = started;
    // link mpc steering
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    initModules();
  }

  /** create Module with standard estimator */
  public MPCKinematicDrivingModule() {
    // track = DubendorfTrack.CHICANE;
    track = null;
    started = Stopwatch.started();
    mpcStateEstimationProvider = new SimpleKinematicMPCStateEstimationProvider(started);
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    initModules();
  }

  private void initModules() {
    // link mpc steering
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcSteering);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcPower);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcBraking);
    lcmMPCPathFollowingClient.registerControlUpdateLister(MPCInformationProvider.getInstance());
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
            (short) -Magnitude.ARMS.toFloat(currents.Get(0)), // sign left invert
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
      Scalar dSteering = mpcSteering.getDotSteering(time);
      if (false) {
        if (Objects.nonNull(steering)) {
          Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
          Scalar difference = steering.subtract(currAngle);
          Scalar torqueCmd = steerPositionController.iterate(difference);
          return Optional.of(SteerPutEvent.createOn(torqueCmd));
        }
      } else {
        if (Objects.nonNull(steering)) {
          Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
          Scalar torqueCmd = steerPositionController.iterate(currAngle, steering, dSteering);
          return Optional.of(SteerPutEvent.createOn(torqueCmd));
        }
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

  private void requestControl() {
    // use joystick for speed limit
    // get joystick
    Scalar maxSpeed = Quantity.of(10, SI.VELOCITY);
    Optional<ManualControlInterface> optionalJoystick = joystickLcmProvider.getManualControl();
    if (optionalJoystick.isPresent()) { // is joystick button "autonomous" pressed?
      ManualControlInterface actualJoystick = optionalJoystick.get();
      Scalar forward = actualJoystick.getAheadPair_Unit().Get(1);
      maxSpeed = mpcPathFollowingConfig.maxSpeed.multiply(forward);
      maxSpeed = Max.of(Quantity.of(1, SI.VELOCITY), maxSpeed);
      // maxSpeed = Quantity.of(1, SI.VELOCITY);
      // System.out.println("got joystick speed value: " + maxSpeed);
    }
    // send message with max speed
    // optimization parameters will have more values in the future
    MPCOptimizationParameter mpcOptimizationParameter = new MPCOptimizationParameter(maxSpeed);
    lcmMPCPathFollowingClient.publishOptimizationParameter(mpcOptimizationParameter);
    // send the newest state and start the update state
    GokartState state = mpcStateEstimationProvider.getState();
    Tensor position = Tensors.of(state.getX(), state.getY());
    MPCPathParameter mpcPathParameter = null;
    MPCPreviewableTrack liveTrack = GokartTrackIdentificationModule.TRACK;
    if (track != null)
      mpcPathParameter = track.getPathParameterPreview(previewSize, position, mpcPathFollowingConfig.padding);
    else if (liveTrack != null)
      mpcPathParameter = liveTrack.getPathParameterPreview(previewSize, position, mpcPathFollowingConfig.padding);
    if (mpcPathParameter != null)
      lcmMPCPathFollowingClient.publishControlRequest(state, mpcPathParameter);
    else
      System.out.println("no Track to drive on! :O");
  }

  @Override
  protected void first() throws Exception {
    lcmMPCPathFollowingClient.start();
    mpcStateEstimationProvider.first();
    joystickLcmProvider.start();
    SteerSocket.INSTANCE.addPutProvider(steerProvider);
    RimoSocket.INSTANCE.addPutProvider(rimoProvider);
    System.out.println("add linmot provider");
    System.out.println(LinmotSocket.INSTANCE.getPutListenersSize());
    LinmotSocket.INSTANCE.addPutProvider(linmotProvider);
    System.out.println(LinmotSocket.INSTANCE.getPutListenersSize());
    // ModuleAuto.INSTANCE.runOne(SpeedLimitSafetyModule.class);
    controlRequestTask = new TimerTask() {
      @Override
      public void run() {
        requestControl();
      }
    };
    System.out.println("Scheduling Timer: start");
    long millis = Magnitude.MILLI_SECOND.toLong(mpcPathFollowingConfig.updateCycle);
    timer.schedule(controlRequestTask, millis, millis); // use update cycle at startup
    lcmMPCPathFollowingClient.registerControlUpdateLister(new MPCControlUpdateListenerWithAction() {
      @Override
      void doAction() {
        // we got an update
        // System.out.println("resheduling timer");
        timer.cancel();
        timer = new Timer();
        controlRequestTask = new TimerTask() {
          @Override
          public void run() {
            requestControl();
          }
        };
        long delay_ms = Magnitude.MILLI_SECOND.toLong(mpcPathFollowingConfig.updateDelay);
        long cycle_ms = Magnitude.MILLI_SECOND.toLong(mpcPathFollowingConfig.updateCycle);
        timer.schedule(controlRequestTask, delay_ms, cycle_ms);
        // (long) (mpcPathFollowingConfig.updateDelay.number().floatValue() * 1000), //
        // (long) (mpcPathFollowingConfig.updateCycle.number().floatValue() * 1000));
      }
    });
  }

  @Override
  protected void last() {
    System.out.println("cancel timer: ending");
    timer.cancel();
    lcmMPCPathFollowingClient.stop();
    mpcStateEstimationProvider.last();
    SteerSocket.INSTANCE.removePutProvider(steerProvider);
    RimoSocket.INSTANCE.removePutProvider(rimoProvider);
    LinmotSocket.INSTANCE.removePutProvider(linmotProvider);
    joystickLcmProvider.stop();
    // ModuleAuto.INSTANCE.terminateOne(SpeedLimitSafetyModule.class);
  }
}
