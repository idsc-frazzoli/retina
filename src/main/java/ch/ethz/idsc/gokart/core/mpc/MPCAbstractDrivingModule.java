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
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;

public abstract class MPCAbstractDrivingModule extends AbstractModule implements MPCBSplineTrackListener, Runnable {
  private final TrackReconModule trackReconModule = //
      ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
  public final LcmMPCControlClient lcmMPCControlClient;
  final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;
  // private final MPCSteering mpcSteering = new MPCOpenLoopSteering();
  private final MPCSteering mpcSteering = new MPCCorrectedOpenLoopSteering();
  // private final MPCBraking mpcBraking = new MPCSimpleBraking();
  // private final MPCBraking mpcBraking = new MPCAggressiveTorqueVectoringBraking();
  private final MPCBraking mpcBraking = new MPCAggressiveCorrectedTorqueVectoringBraking();
  private final MPCPower mpcPower;
  private final MPCStateEstimationProvider mpcStateEstimationProvider;
  private final Thread thread = new Thread(this);
  private boolean running = true;
  // private final Timing timing;
  // private boolean useTorqueVectoring;
  private final int previewSize = MPCNative.SPLINE_PREVIEW_SIZE;
  private Optional<MPCBSplineTrack> mpcBSplineTrack = Optional.empty();
  private final MPCPreviewableTrack track;
  final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  final MPCRimoProvider mpcRimoProvider;
  private final MPCSteerProvider mpcSteerProvider;
  final MPCLinmotProvider mpcLinmotProvider;

  /** switch to testing binary that send back test data has to be called before first */
  public void switchToTest() {
    lcmMPCControlClient.switchToTest();
  }

  /** create Module with standard estimator */
  MPCAbstractDrivingModule(LcmMPCControlClient lcmMPCPathFollowingClient, Timing timing) {
    // using dynamic is not a mistake here:
    this(lcmMPCPathFollowingClient, new SimpleDynamicMPCStateEstimationProvider(timing), timing, null);
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

  abstract MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering);

  private final void initModules() {
    // link mpc steering
    lcmMPCControlClient.registerControlUpdateLister(mpcSteering);
    lcmMPCControlClient.registerControlUpdateLister(mpcPower);
    lcmMPCControlClient.registerControlUpdateLister(mpcBraking);
    lcmMPCControlClient.registerControlUpdateLister(MPCInformationProvider.getInstance());
    // lcmMPCPathFollowingClient.registerControlUpdateLister(MPCActiveCompensationLearning.getInstance());
    // state estimation provider
    mpcBraking.setStateEstimationProvider(mpcStateEstimationProvider);
    // mpcPower.setStateEstimationProvider(mpcStateEstimationProvider);
    mpcSteering.setStateEstimationProvider(mpcStateEstimationProvider);
  }

  abstract MPCOptimizationParameter createOptimizationParameter();

  private final void requestControl() {
    MPCOptimizationParameter mpcOptimizationParameter = createOptimizationParameter();
    lcmMPCControlClient.publishOptimizationParameter(mpcOptimizationParameter);
    // send the newest state and start the update state
    GokartState state = mpcStateEstimationProvider.getState();
    Tensor safetyRadiusPosition = state.getCenterPosition();
    MPCPathParameter mpcPathParameter = null;
    MPCPreviewableTrack liveTrack = mpcBSplineTrack.orElse(null);
    // Objects.isNull(gokartTrackReconModule) //
    // ? null
    // : gokartTrackReconModule.getMPCBSplineTrack();
    Scalar padding = MPCOptimizationConfig.GLOBAL.padding;
    Scalar qpFactor = MPCOptimizationConfig.GLOBAL.qpFactor;
    if (Objects.nonNull(track))
      mpcPathParameter = track.getPathParameterPreview(previewSize, safetyRadiusPosition, padding, qpFactor);
    else //
    if (Objects.nonNull(liveTrack))
      mpcPathParameter = liveTrack.getPathParameterPreview(previewSize, safetyRadiusPosition, padding, qpFactor);
    if (Objects.nonNull(mpcPathParameter))
      lcmMPCControlClient.publishControlRequest(state, mpcPathParameter);
    else
      System.out.println("no Track to drive on! :O");
  }

  @Override
  protected final void first() {
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersAdd(this);
    else
      System.err.println("did not subscribe to track info !!!");
    // ---
    lcmMPCControlClient.start();
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
    mpcBraking.start();
    mpcSteering.start();
    mpcPower.start();
    lcmMPCControlClient.registerControlUpdateLister(new MPCControlUpdateListenerWithAction() {
      @Override
      void doAction() {
        // we got an update
        // interupt
        thread.interrupt();
      }

      @Override
      public void start() {
        // TODO MH document that empty implementation is desired
      }

      @Override
      public void stop() {
        // TODO MH document that empty implementation is desired
      }
    });
    thread.start();
    // ---
    System.out.println("Scheduling Timer: start");
  }

  @Override
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
    mpcSteering.stop();
    mpcPower.stop();
    // MPCActiveCompensationLearning.getInstance().setActive(false);
    // ---
    lcmMPCControlClient.stop();
    mpcStateEstimationProvider.last();
    manualControlProvider.stop();
    // ---
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersRemove(this);
  }

  @Override // from MPCBSplineTrackListener
  public final void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    System.out.println("kinematic mpc bspline track, present=" + optional.isPresent());
    this.mpcBSplineTrack = optional;
  }

  @Override
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
}
