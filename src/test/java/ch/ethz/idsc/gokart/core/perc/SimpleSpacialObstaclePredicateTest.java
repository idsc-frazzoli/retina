// code by vc, jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SimpleSpacialObstaclePredicateTest extends TestCase {
  public void testSimple() {
    SpacialObstaclePredicate sop = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
    Tensor lidar_p1 = Tensors.vector(3.0, -1.2, -0.2); // coordinates in lidar frame
    assertTrue(sop.isObstacle(lidar_p1)); // lidar_p1 should classify as obstacle, etc.
    Tensor lidar_p2 = Tensors.vector(3.0, -1.2, 0.3); // coordinates in lidar frame
    Tensor lidar_p3 = Tensors.vector(1, 1, 0); // coordinates in lidar frame
    assertTrue(sop.isObstacle(lidar_p3));
    assertFalse(sop.isObstacle(lidar_p2)); // lidar_p2 should classify not as obstacle (?)
  }

  public void testSimpleXZNear() {
    SpacialXZObstaclePredicate sop = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
    assertTrue(sop.isObstacle(0, 0));
    assertFalse(sop.isObstacle(0, 1));
    assertFalse(sop.isObstacle(0, 2));
    assertTrue(sop.isObstacle(0, -0.5f));
    assertFalse(sop.isObstacle(0, -1.1f));
  }

  public void testSimpleXZFarFront() {
    SpacialXZObstaclePredicate sop = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
    // double z_corrected = z - x * inc; // negative sign
    assertTrue(sop.isObstacle(100, 100 * 0.04f));
    assertFalse(sop.isObstacle(100, 100 * 0.04f + 0.6f));
    assertFalse(sop.isObstacle(100, 100 * 0.04f - 1.2f));
  }

  public void testSimpleXZFarBack() {
    SpacialXZObstaclePredicate sop = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
    // double z_corrected = z - x * inc; // negative sign
    assertTrue(sop.isObstacle(-100, -100 * 0.04f));
    assertFalse(sop.isObstacle(-100, -100 * 0.04f + 0.6f));
    assertFalse(sop.isObstacle(-100, -100 * 0.04f - 1.2f));
  }
}
