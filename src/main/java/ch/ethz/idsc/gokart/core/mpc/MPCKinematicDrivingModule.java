package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class MPCKinematicDrivingModule extends AbstractModule {
  public final LcmMPCPathFollowingClient lcmMPCPathFollowingClient//
      = new LcmMPCPathFollowingClient();

  //public final PutProvider<T>
  
  @Override
  protected void first() throws Exception {
    lcmMPCPathFollowingClient.start();
  }

  @Override
  protected void last() {
    lcmMPCPathFollowingClient.stop();
  }
}
