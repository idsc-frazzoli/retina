// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartTrajectoryModuleTest extends TestCase {
  public void testSimple() throws Exception {
    GokartTrajectoryModule gtm = new GokartTrajectoryModule();
    gtm.first();
    assertTrue(Objects.nonNull(gtm.obstacleMap));
    assertTrue(Objects.nonNull(gtm.waypoints));
    gtm.last();
  }

  public void testPose() throws Exception {
    GokartTrajectoryModule gtm = new GokartTrajectoryModule();
    gtm.first();
    GokartPoseEvent gpe = GokartPoseEvents.getPoseEvent(Tensors.fromString("{36.8[m], 44.2[m], 0.8}"), RealScalar.ONE);
    GokartPoseLcmServer.INSTANCE.publish(gpe);
    Thread.sleep(50);
    gtm.runAlgo();
    Thread.sleep(500);
    gtm.last();
  }
}
