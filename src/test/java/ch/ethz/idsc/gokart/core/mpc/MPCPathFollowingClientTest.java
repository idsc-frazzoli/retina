package ch.ethz.idsc.gokart.core.mpc;

import junit.framework.TestCase;

public class MPCPathFollowingClientTest extends TestCase {
  public void testSimple() throws Exception {
     MPCPathFollowingClient mpcPathFollowingClient = new MPCPathFollowingClient(MPCPathFollowingConfig.GLOBAL);
     mpcPathFollowingClient.first();
     Thread.sleep(100000);
     mpcPathFollowingClient.last();
  }
}
