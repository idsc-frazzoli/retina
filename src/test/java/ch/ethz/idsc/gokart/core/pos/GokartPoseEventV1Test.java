// code by jph
package ch.ethz.idsc.gokart.core.pos;

import junit.framework.TestCase;

public class GokartPoseEventV1Test extends TestCase {
  public void testLength() {
    assertEquals(GokartPoseEventV1.LENGTH, 24 + 4);
  }
}
