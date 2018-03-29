// code by vc, jph
package ch.ethz.idsc.gokart.core.fuse.perc;

import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialObstaclePredicate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SimpleSpacialObstaclePredicateTest extends TestCase {
  public void testSimple() {
    SpacialObstaclePredicate sop = SimpleSpacialObstaclePredicate.createVlp16();
    Tensor lidar_p1 = Tensors.vector(3.0, -1.2, -0.2); // coordinates in lidar frame
    assertTrue(sop.isObstacle(lidar_p1)); // lidar_p1 should classify as obstacle, etc.
    Tensor lidar_p2 = Tensors.vector(3.0, -1.2, 0.3); // coordinates in lidar frame
    Tensor lidar_p3 = Tensors.vector(1, 1, 0); // coordinates in lidar frame
    assertTrue(sop.isObstacle(lidar_p3));
    assertFalse(sop.isObstacle(lidar_p2)); // lidar_p2 should classify not as obstacle (?)
  }
}
