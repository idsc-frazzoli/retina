// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartPoseEventsTest extends TestCase {
  public void testMotionless() {
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
    assertTrue(gokartPoseEvent.hasVelocity());
    assertEquals(gokartPoseEvent.asVector(), Tensors.vector(0, 0, 0, 0, 0, 0, 0));
    assertTrue(gokartPoseEvent instanceof GokartPoseEventV2);
  }

  public void testExtended() {
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.create( //
        Tensors.fromString("{1[m], 2[m], 3}"), RealScalar.ONE, //
        Tensors.fromString("{4[m*s^-1], 5[m*s^-1], 6[s^-1]}"));
    assertEquals(gokartPoseEvent.getVelocity(), Tensors.fromString("{4[m*s^-1], 5[m*s^-1], 6[s^-1]}"));
    assertEquals(gokartPoseEvent.getGyroZ(), Quantity.of(6, SI.PER_SECOND));
    assertTrue(gokartPoseEvent.hasVelocity());
    assertEquals(gokartPoseEvent.asVector(), Tensors.vector(1, 2, 3, 1, 4, 5, 6));
  }

  /***************************************************/
  public void testCreateV1() {
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(PoseHelper.attachUnits(Tensors.vector(1, 2, 3)), RealScalar.ONE);
    assertFalse(gokartPoseEvent instanceof GokartPoseEventV2);
  }

  public void testSimple() {
    GokartPoseEvent gokartPoseEvent = GokartPoseEvents.offlineV1( //
        Tensors.fromString("{1[m], 2[m], 3}"), RealScalar.ONE);
    assertEquals(gokartPoseEvent.getVelocity(), Tensors.fromString("{0[m*s^-1], 0[m*s^-1], 0[s^-1]}"));
    assertEquals(gokartPoseEvent.getGyroZ(), Quantity.of(0, SI.PER_SECOND));
    assertFalse(gokartPoseEvent.hasVelocity());
    assertEquals(gokartPoseEvent.asVector(), Tensors.vector(1, 2, 3, 1));
  }

  public void testNullFail() {
    try {
      GokartPoseEvents.offlineV1(null, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
