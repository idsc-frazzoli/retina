// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.util.Objects;

import junit.framework.TestCase;

public class GokartPoseLcmServerTest extends TestCase {
  public void testSimple() {
    assertTrue(Objects.nonNull(GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry()));
  }
}
