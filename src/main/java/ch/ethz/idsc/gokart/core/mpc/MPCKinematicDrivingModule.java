package ch.ethz.idsc.gokart.core.mpc;

//Not in use yet
import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCKinematicDrivingModule extends AbstractModule implements MPCControlUpdateListener {
  public final LcmMPCControlClient lcmMPCPathFollowingClient//
      = new LcmMPCControlClient();
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final MPCSteering mpcSteering = new MPCOpenLoopSteering();
  private final MPCBraking mpcBraking = new MPCSimpleBraking();
  private final MPCPower mpcPower;
  private final SteerPositionControl steerPositionController = new SteerPositionControl();
  private final Stopwatch started = Stopwatch.started();

  public MPCKinematicDrivingModule() {
    // link mpc steering
    mpcPower = new MPCTorqueVectoringPower(mpcSteering);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcSteering);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcPower);
    lcmMPCPathFollowingClient.registerControlUpdateLister(mpcBraking);
  }

  public final PutProvider<RimoPutEvent> rimoProvider = new PutProvider<RimoPutEvent>() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      Scalar time = Quantity.of(started.display_seconds(), SI.SECOND);
      Tensor currents = mpcPower.getPower(time);
      if (currents != null)
        return Optional.of(RimoPutHelper.operationTorque( //
            (short) Magnitude.ARMS.toFloat(currents.Get(0)), // sign left invert
            (short) Magnitude.ARMS.toFloat(currents.Get(1)) // sign right id
        ));
      else
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
      if (steering != null) {
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
      if (braking != null)
        return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(braking));
      else
        return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.ZERO));
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.AUTONOMOUS;
    }
  };

  @Override
  protected void first() throws Exception {
    lcmMPCPathFollowingClient.start();
  }

  @Override
  protected void last() {
    lcmMPCPathFollowingClient.stop();
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
  }
}
