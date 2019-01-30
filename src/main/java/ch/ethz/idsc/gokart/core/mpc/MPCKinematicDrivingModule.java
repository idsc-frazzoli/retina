// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
//Not in use yet
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;

public class MPCKinematicDrivingModule extends AbstractModule implements MPCBSplineTrackListener {
  private final GokartTrackReconModule gokartTrackReconModule = //
      ModuleAuto.INSTANCE.getInstance(GokartTrackReconModule.class);
  public final LcmMPCControlClient lcmMPCPathFollowingClient = new LcmMPCControlClient();
  private final MPCOptimizationConfig mpcPathFollowingConfig = MPCOptimizationConfig.GLOBAL;
  private final MPCSteering mpcSteering = new MPCOpenLoopSteering();
  private final MPCBraking mpcBraking = new MPCSimpleBraking();
  private final MPCPower mpcPower;
  private final MPCStateEstimationProvider mpcStateEstimationProvider;
  private final Timing timing;
  // private boolean useTorqueVectoring;
  private Timer timer = new Timer();
  private final int previewSize = MPCNative.SPLINEPREVIEWSIZE;
  private Optional<MPCBSplineTrack> mpcBSplineTrack = Optional.empty();
  private final MPCPreviewableTrack track;
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private TimerTask controlRequestTask;
  final MPCRimoProvider mpcRimoProvider;
  private final MPCSteerProvider mpcSteerProvider;
  final MPCLinmotProvider mpcLinmotProvider;

  /** switch to testing binary that send back test data has to be called before first */
  public void switchToTest() {
    lcmMPCPathFollowingClient.switchToTest();
  }

  /** Hint: constructor only for testing
   * create Module with custom estimator
   * 
   * @param estimator the custom estimator
   * @param timing that shows the same time that also was used for the custom estimator */
  MPCKinematicDrivingModule(MPCStateEstimationProvider estimator, Timing timing, MPCPreviewableTrack track) {
    mpcStateEstimationProvider = estimator;
    this.timing = timing;
    this.track = track;
    // link mpc steering
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    mpcRimoProvider = new MPCRimoProvider(timing, mpcPower);
    mpcLinmotProvider = new MPCLinmotProvider(timing, mpcBraking);
    mpcSteerProvider = new MPCSteerProvider(timing, mpcSteering);
    initModules();
  }

  /** create Module with standard estimator */
  public MPCKinematicDrivingModule() {
    track = null;
    timing = Timing.started();
    mpcStateEstimationProvider = new SimpleKinematicMPCStateEstimationProvider(timing);
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    mpcRimoProvider = new MPCRimoProvider(timing, mpcPower);
    mpcLinmotProvider = new MPCLinmotProvider(timing, mpcBraking);
    mpcSteerProvider = new MPCSteerProvider(timing, mpcSteering);
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

  private void requestControl() {
    // use joystick for speed limit
    // get joystick
    Scalar maxSpeed = Quantity.of(10, SI.VELOCITY);
    Scalar maxXacc = MPCOptimizationConfig.GLOBAL.maxLonAcc;
    Scalar maxYacc = MPCOptimizationConfig.GLOBAL.maxLatAcc;
    Scalar latAccLim = MPCOptimizationConfig.GLOBAL.latAccLim;
    Scalar rotAccEffect = MPCOptimizationConfig.GLOBAL.rotAccEffect;
    Scalar torqueVecEffect = MPCOptimizationConfig.GLOBAL.torqueVecEffect;
    Scalar brakeEffect = MPCOptimizationConfig.GLOBAL.brakeEffect;
    Scalar padding = MPCOptimizationConfig.GLOBAL.padding;
    Scalar qpFactor = MPCOptimizationConfig.GLOBAL.qpFactor;
    Optional<ManualControlInterface> optionalJoystick = manualControlProvider.getManualControl();
    if (optionalJoystick.isPresent()) { // is joystick button "autonomoRus" pressed?
      ManualControlInterface actualJoystick = optionalJoystick.get();
      Scalar forward = actualJoystick.getAheadPair_Unit().Get(1);
      maxSpeed = mpcPathFollowingConfig.maxSpeed.multiply(forward);
      // TODO MH move min speed to config
      maxSpeed = Max.of(Quantity.of(0.2, SI.VELOCITY), maxSpeed);
      // maxSpeed = Quantity.of(1, SI.VELOCITY);
      // System.out.println("got joystick speed value: " + maxSpeed);
    }
    // send message with max speed
    // optimization parameters will have more values in the future
    // MPCOptimizationParameter mpcOptimizationParameter = new MPCOptimizationParameter(maxSpeed, maxXacc, maxYacc);
    MPCOptimizationParameter mpcOptimizationParameter//
        = new MPCOptimizationParameter(maxSpeed, maxXacc, maxYacc, //
            latAccLim, rotAccEffect, torqueVecEffect, brakeEffect);
    lcmMPCPathFollowingClient.publishOptimizationParameter(mpcOptimizationParameter);
    // send the newest state and start the update state
    GokartState state = mpcStateEstimationProvider.getState();
    Tensor position = Tensors.of(state.getX(), state.getY());
    MPCPathParameter mpcPathParameter = null;
    MPCPreviewableTrack liveTrack = mpcBSplineTrack.orElse(null);
    // Objects.isNull(gokartTrackReconModule) //
    // ? null
    // : gokartTrackReconModule.getMPCBSplineTrack();
    if (Objects.nonNull(track))
      mpcPathParameter = track.getPathParameterPreview(previewSize, position, padding, qpFactor);
    else //
    if (Objects.nonNull(liveTrack))
      mpcPathParameter = liveTrack.getPathParameterPreview(previewSize, position, padding, qpFactor);
    if (Objects.nonNull(mpcPathParameter))
      lcmMPCPathFollowingClient.publishControlRequest(state, mpcPathParameter);
    else
      System.out.println("no Track to drive on! :O");
  }

  @Override
  protected void first() throws Exception {
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.listenersAdd(this);
    // ---
    lcmMPCPathFollowingClient.start();
    mpcStateEstimationProvider.first();
    manualControlProvider.start();
    // ---
    SteerSocket.INSTANCE.addPutProvider(mpcSteerProvider);
    // ---
    RimoSocket.INSTANCE.addPutProvider(mpcRimoProvider);
    // ---
    LinmotSocket.INSTANCE.addPutProvider(mpcLinmotProvider);
    // ---
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
        // System.out.println("re-scheduling timer");
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
      }
    });
  }

  @Override
  protected void last() {
    System.out.println("cancel timer: ending");
    timer.cancel();
    LinmotSocket.INSTANCE.removePutProvider(mpcLinmotProvider);
    // ---
    SteerSocket.INSTANCE.removePutProvider(mpcSteerProvider);
    // ---
    RimoSocket.INSTANCE.removePutProvider(mpcRimoProvider);
    // ---
    lcmMPCPathFollowingClient.stop();
    mpcStateEstimationProvider.last();
    manualControlProvider.stop();
    // ---
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.listenersRemove(this);
  }

  @Override // from MPCBSplineTrackListener
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    System.out.println("kinematic mpc bspline track, present=" + optional.isPresent());
    this.mpcBSplineTrack = optional;
  }
}
