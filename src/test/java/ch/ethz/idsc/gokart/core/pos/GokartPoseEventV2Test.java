// code by jph
package ch.ethz.idsc.gokart.core.pos;

import junit.framework.TestCase;

public class GokartPoseEventV2Test extends TestCase {
  public void testLength() {
    assertEquals(GokartPoseEventV2.LENGTH, 24 + 4 + 12);
  }
}
