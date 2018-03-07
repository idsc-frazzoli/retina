package ch.ethz.idsc.gokart.core.fuse.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SimpleSpacialObstaclePredicateTest extends TestCase {
  public void testSimple() {
    // TODO VC
    // create instance of SimpleSpacialObstaclePredicate
    // SimpleSpacialObstaclePredicate.createVlp16();
    Tensor lidar_p1 = Tensors.vector(3.0, -1.2, -0.2); // coordinates in lidar frame
    // lidar_p1 should classify as obstacle, etc.
    Tensor lidar_p2 = Tensors.vector(3.0, -1.2, 0.3); // coordinates in lidar frame
    // lidar_p2 should classify not as obstacle (?)
    // assertTrue(...);
  }
}
