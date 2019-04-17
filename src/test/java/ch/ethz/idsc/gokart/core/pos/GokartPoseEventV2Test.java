// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GokartPoseEventV2Test extends TestCase {
  public void testLength() {
    assertEquals(GokartPoseEventV2.LENGTH, 24 + 4 + 12);
  }

  public void testVelocity() {
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.create( //
        Tensors.fromString("{1[m], 2[m], 3}"), //
        RealScalar.of(0.75), //
        Tensors.fromString("{4[m*s^-1], 5[m*s^-1],6[s^-1]}"));
    assertTrue(gokartPoseEvent instanceof GokartPoseEventV2);
    Chop._07.requireClose(gokartPoseEvent.getVelocity(), Tensors.fromString("{4[m*s^-1], 5[m*s^-1], 6[s^-1]}"));
    Chop._07.requireClose(gokartPoseEvent.getVelocityXY(), Tensors.fromString("{4[m*s^-1], 5[m*s^-1]}"));
    Chop._07.requireClose(gokartPoseEvent.getGyroZ(), Tensors.fromString("6[s^-1]"));
  }
}
