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

public abstract class MPCAbstractDrivingModule extends AbstractModule implements //
    MPCBSplineTrackListener, Runnable {
  private final TrackReconModule trackReconModule = //
      ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
  private final LcmMPCControlClient lcmMPCControlClient;
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;
  // private final MPCSteering mpcSteering = new MPCOpenLoopSteering();
  private final MPCSteering mpcSteering = new MPCCorrectedOpenLoopSteering();
  // private final MPCBraking mpcBraking = new MPCSimpleBraking();
  // private final MPCBraking mpcBraking = new MPCAggressiveTorqueVectoringBraking();
  private final MPCAggressiveCorrectedTorqueVectoringBraking mpcBraking = new MPCAggressiveCorrectedTorqueVectoringBraking();
  private final MPCPower mpcPower;
  private final MPCStateEstimationProvider mpcStateEstimationProvider;
  private final Thread thread = new Thread(this);
  private final int previewSize = MPCNative.SPLINE_PREVIEW_SIZE;
  private final MPCPreviewableTrack track;
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final MPCSteerProvider mpcSteerProvider;
  // ---
  final MPCRimoProvider mpcRimoProvider;
  final MPCLinmotProvider mpcLinmotProvider;
  // ---
  private boolean running = true;
  private Optional<MPCBSplineTrack> mpcBSplineTrack = Optional.empty();

  /** switch to testing binary that send back test data has to be called before first */
  public void switchToTest() {
    lcmMPCControlClient.switchToTest();
  }

  /** create Module with standard estimator */
  MPCAbstractDrivingModule(LcmMPCControlClient lcmMPCControlClient, Timing timing) {
    this(lcmMPCControlClient, //
        new SimpleDynamicMPCStateEstimationProvider(timing), // the use of "dynamic" is intended
        timing, null);
  }

  /** Hint: constructor only for testing
   * create Module with custom estimator
   * 
   * @param mpcStateEstimationProvider the custom estimator
   * @param timing that shows the same time that also was used for the custom estimator */
  MPCAbstractDrivingModule( //
      LcmMPCControlClient lcmMPCControlClient, //
      MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    this.lcmMPCControlClient = lcmMPCControlClient;
    this.mpcStateEstimationProvider = mpcStateEstimationProvider;
    this.track = track;
    // link mpc steering
    // mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    mpcPower = createPower(mpcStateEstimationProvider, mpcSteering);
    mpcRimoProvider = new MPCRimoProvider(timing, mpcPower);
    mpcLinmotProvider = new MPCLinmotProvider(timing, mpcBraking);
    mpcSteerProvider = new MPCSteerProvider(timing, mpcSteering);
    initModules();
  }

  private final void initModules() {
    // link mpc steering
    lcmMPCControlClient.addControlUpdateListener(mpcSteering);
    lcmMPCControlClient.addControlUpdateListener(mpcPower);
    lcmMPCControlClient.addControlUpdateListener(mpcBraking);
    // lcmMPCControlClient.addControlUpdateListener(MPCInformationProvider.getInstance());
    // lcmMPCPathFollowingClient.registerControlUpdateLister(MPCActiveCompensationLearning.getInstance());
    // state estimation provider
    mpcBraking.setStateEstimationProvider(mpcStateEstimationProvider);
    // mpcPower.setStateEstimationProvider(mpcStateEstimationProvider);
    mpcSteering.setStateEstimationProvider(mpcStateEstimationProvider);
  }

  private final void requestControl() {
    MPCOptimizationParameter mpcOptimizationParameter = //
        createOptimizationParameter(mpcOptimizationConfig, manualControlProvider.getManualControl());
    lcmMPCControlClient.publishOptimizationParameter(mpcOptimizationParameter);
    // send the newest state and start the update state
    GokartState state = mpcStateEstimationProvider.getState();
    Tensor safetyRadiusPosition = state.getCenterPosition();
    MPCPathParameter mpcPathParameter = null;
    MPCPreviewableTrack liveTrack = mpcBSplineTrack.orElse(null);
    Scalar padding = MPCOptimizationConfig.GLOBAL.padding;
    Scalar qpFactor = MPCOptimizationConfig.GLOBAL.qpFactor;
    Scalar qpLimit = MPCOptimizationConfig.GLOBAL.qpLimit;
    if (Objects.nonNull(track))
      mpcPathParameter = track.getPathParameterPreview(previewSize, safetyRadiusPosition, padding, qpFactor, qpLimit);
    else //
    if (Objects.nonNull(liveTrack))
      mpcPathParameter = liveTrack.getPathParameterPreview(previewSize, safetyRadiusPosition, padding, qpFactor, qpLimit);
    if (Objects.nonNull(mpcPathParameter))
      lcmMPCControlClient.publishControlRequest(state, mpcPathParameter);
    else
      System.out.println("no Track to drive on! :O");
  }

  @Override // from AbstractModule
  protected final void first() {
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersAdd(this);
    else
      System.err.println("did not subscribe to track info !!!");
    // ---
    lcmMPCControlClient.start();
    mpcStateEstimationProvider.first();
    manualControlProvider.start();
    // ---
    SteerSocket.INSTANCE.addPutProvider(mpcSteerProvider);
    RimoSocket.INSTANCE.addPutProvider(mpcRimoProvider);
    LinmotSocket.INSTANCE.addPutProvider(mpcLinmotProvider);
    //
    mpcBraking.start();
    lcmMPCControlClient.addControlUpdateListener(new MPCControlUpdateInterrupt(thread));
    thread.start();
    // ---
    System.out.println("Scheduling Timer: start");
  }

  @Override // from AbstractModule
  protected final void last() {
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
    mpcBraking.stop();
    // ---
    lcmMPCControlClient.stop();
    mpcStateEstimationProvider.last();
    manualControlProvider.stop();
    // ---
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersRemove(this);
  }

  /***************************************************/
  @Override // from MPCBSplineTrackListener
  public final void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    System.out.println("kinematic mpc bspline track, present=" + optional.isPresent());
    this.mpcBSplineTrack = optional;
  }

  @Override // from Runnable
  public final void run() {
    while (running) {
      requestControl();
      try {
        Thread.sleep(Magnitude.MILLI_SECOND.toLong(mpcOptimizationConfig.updateCycle));
      } catch (InterruptedException e) {
        // sleep is interrupted once data arrives
      }
    }
    System.out.println("Thread terminated");
  }

  /***************************************************/
  /** @param mpcOptimizationConfig non-null
   * @param optional
   * @return */
  abstract MPCOptimizationParameter createOptimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional);

  /** @param mpcStateEstimationProvider
   * @param mpcSteering
   * @return */
  abstract MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering);
}
