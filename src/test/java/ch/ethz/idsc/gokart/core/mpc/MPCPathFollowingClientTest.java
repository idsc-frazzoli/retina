package ch.ethz.idsc.gokart.core.mpc;

import junit.framework.TestCase;

public class MPCPathFollowingClientTest extends TestCase {
  public void testSimple() throws Exception {
     MPCPathFollowingClient mpcPathFollowingClient = new MPCPathFollowingClient(MPCPathFollowingConfig.GLOBAL);
     mpcPathFollowingClient.first();
     Thread.sleep(10000);//fire for a second
     mpcPathFollowingClient.last();
  }
}
