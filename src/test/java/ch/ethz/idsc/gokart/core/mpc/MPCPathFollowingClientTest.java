// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import junit.framework.TestCase;

public class MPCPathFollowingClientTest extends TestCase {
  public void testSimple() throws Exception {
    // TODO
    LcmMPCPathFollowingClient lcmMPCPathFollowingClient =
        new LcmMPCPathFollowingClient();
    lcmMPCPathFollowingClient.start();
    Thread.sleep(10000); // fire for a second
    lcmMPCPathFollowingClient.stop();
  }
}
