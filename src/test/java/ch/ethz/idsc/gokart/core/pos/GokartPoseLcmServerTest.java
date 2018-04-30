// code by jph
package ch.ethz.idsc.gokart.core.pos;

import junit.framework.TestCase;

public class GokartPoseLcmServerTest extends TestCase {
  public void testSimple() {
    assertNotNull(GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry());
  }
}
