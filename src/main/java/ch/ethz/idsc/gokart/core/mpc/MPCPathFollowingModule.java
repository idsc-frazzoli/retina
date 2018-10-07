// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.gokart.core.fuse.SpeedLimitSafetyModule;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.ModuleAuto;

public class MPCPathFollowingModule extends AbstractModule {
  private final MPCPathFollowingClient mpcPathFollowingClient = SocketMPCPathFollowingClient.create();

  @Override
  protected void first() throws Exception {
    ModuleAuto.INSTANCE.runOne(SpeedLimitSafetyModule.class);
    mpcPathFollowingClient.start();
  }

  @Override
  protected void last() {
    mpcPathFollowingClient.stop();
    ModuleAuto.INSTANCE.terminateOne(SpeedLimitSafetyModule.class);
  }
}
