// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
//Not in use yet
import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCKinematicDrivingModule extends AbstractModule implements MPCBSplineTrackListener, Runnable {
  private final TrackReconModule gokartTrackReconModule = //
      ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
  public final LcmMPCControlClient lcmMPCPathFollowingClient = new LcmMPCControlClient();
  private final MPCOptimizationConfig mpcPathFollowingConfig = MPCOptimizationConfig.GLOBAL;
  // private final MPCSteering mpcSteering = new MPCOpenLoopSteering();
  private final MPCSteering mpcSteering = new MPCCorrectedOpenLoopSteering();
  // private final MPCBraking mpcBraking = new MPCSimpleBraking();
  // private final MPCBraking mpcBraking = new MPCAggressiveTorqueVectoringBraking();
  private final MPCBraking mpcBraking = new MPCAggressiveTorqueVectoringBraking();
  private final MPCPower mpcPower;
  private final MPCStateEstimationProvider mpcStateEstimationProvider;
  private final Thread thread = new Thread(this);
  private boolean running = true;
  // private final Timing timing;
  // private boolean useTorqueVectoring;
  private final int previewSize = MPCNative.SPLINE_PREVIEW_SIZE;
  private Optional<MPCBSplineTrack> mpcBSplineTrack = Optional.empty();
  private final MPCPreviewableTrack track;
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  final MPCRimoProvider mpcRimoProvider;
  private final MPCSteerProvider mpcSteerProvider;
  final MPCLinmotProvider mpcLinmotProvider;

  /** switch to testing binary that send back test data has to be called before first */
  public void switchToTest() {
    lcmMPCPathFollowingClient.switchToTest();
  }

  /** create Module with standard estimator */
  public MPCKinematicDrivingModule() {
    this(Timing.started());
  }

  MPCKinematicDrivingModule(Timing timing) {
    // this(new SimpleKinematicMPCStateEstimationProvider(timing), timing, null);
    this(new SimpleDynamicMPCStateEstimationProvider(timing), timing, null);
  }

  /** Hint: constructor only for testing
   * create Module with custom estimator
   * 
   * @param estimator the custom estimator
   * @param timing that shows the same time that also was used for the custom estimator */
  MPCKinematicDrivingModule(MPCStateEstimationProvider estimator, Timing timing, MPCPreviewableTrack track) {
    mpcStateEstimationProvider = estimator;
    this.track = track;
    // this.timing = timing;
    // link mpc steering
    // mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    mpcPower = new MPCAggressiveTorqueVectoringPower(mpcSteering);
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
    // lcmMPCPathFollowingClient.registerControlUpdateLister(MPCActiveCompensationLearning.getInstance());
    // state estimation provider
    mpcBraking.setStateProvider(mpcStateEstimationProvider);
    mpcPower.setStateProvider(mpcStateEstimationProvider);
    mpcSteering.setStateProvider(mpcStateEstimationProvider);
  }

  private void requestControl() {
    // use joystick for speed limit
    // get joystick
    Scalar maxSpeed = MPCOptimizationConfig.GLOBAL.maxSpeed;
    Scalar minSpeed = MPCOptimizationConfig.GLOBAL.minSpeed;
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
      maxSpeed = Max.of(minSpeed, maxSpeed);
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
    Tensor position = state.getCenterPosition();
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
  protected void first() {
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.listenersAdd(this);
    // ---
    lcmMPCPathFollowingClient.start();
    mpcStateEstimationProvider.first();
    // MPCActiveCompensationLearning.getInstance().setActive(true);
    manualControlProvider.start();
    // ---
    SteerSocket.INSTANCE.addPutProvider(mpcSteerProvider);
    // ---
    RimoSocket.INSTANCE.addPutProvider(mpcRimoProvider);
    // ---
    LinmotSocket.INSTANCE.addPutProvider(mpcLinmotProvider);
    //
    lcmMPCPathFollowingClient.registerControlUpdateLister(new MPCControlUpdateListenerWithAction() {
      @Override
      void doAction() {
        // we got an update
        // interupt
        thread.interrupt();
      }
    });
    thread.start();
    // ---
    System.out.println("Scheduling Timer: start");
  }

  @Override
  protected void last() {
    System.out.println("cancel timer: ending");
    running = false;
    thread.interrupt();
    // ---
    LinmotSocket.INSTANCE.removePutProvider(mpcLinmotProvider);
    // ---
    SteerSocket.INSTANCE.removePutProvider(mpcSteerProvider);
    // ---
    RimoSocket.INSTANCE.removePutProvider(mpcRimoProvider);
    //
    // MPCActiveCompensationLearning.getInstance().setActive(false);
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

  @Override
  public void run() {
    while (running) {
      requestControl();
      try {
        Thread.sleep(Magnitude.MILLI_SECOND.toLong(mpcPathFollowingConfig.updateCycle));
      } catch (InterruptedException e) {
        // sleep is interrupted once data arrives
      }
    }
    System.out.println("Thread terminated");
  }
}
