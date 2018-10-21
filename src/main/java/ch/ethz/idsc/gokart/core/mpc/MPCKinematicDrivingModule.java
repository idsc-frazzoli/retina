package ch.ethz.idsc.gokart.core.mpc;
//Not in use yet
import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.mpc.LcmMPCControlClient.MPCControlUpdateListener;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;

public class MPCKinematicDrivingModule extends AbstractModule implements MPCControlUpdateListener {
  public final LcmMPCControlClient lcmMPCPathFollowingClient//
      = new LcmMPCControlClient();
  private MPCSteering mpcSteering = new MPCOpenLoopSteering();
  
  
  
  public final PutProvider<RimoPutEvent> rimoProvider = new PutProvider<RimoPutEvent>() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      return Optional.of(RimoPutHelper.operationTorque( //
          (short) 0, // sign left invert
          (short) 0 // sign right id
      ));
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.AUTONOMOUS;
    }
  };
  public final PutProvider<SteerPutEvent> steerProvider = new PutProvider<SteerPutEvent>() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      return Optional.of(null);
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.AUTONOMOUS;
    }
  };
  public final PutProvider<LinmotPutEvent> linmotProvider = new PutProvider<LinmotPutEvent>() {
    @Override
    public Optional<LinmotPutEvent> putEvent() {
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
    mpcSteering.Update(controlAndPredictionSteps);
  }
}
