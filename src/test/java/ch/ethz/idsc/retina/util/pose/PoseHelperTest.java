// code by jph, mcp
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PoseHelperTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor state = PoseHelper.attachUnits(vector);
    assertEquals(vector, PoseHelper.toUnitless(state));
  }

  public void testUnits() {
    Tensor state = Tensors.fromString("{1[m], 2[m], 3}");
    Tensor vector = PoseHelper.toUnitless(state);
    assertEquals(state, PoseHelper.attachUnits(vector));
  }

  public void testPose() {
    Tensor pose = Tensors.fromString("{6.2, 4.2, 1}");
    Tensor poseConv = PoseHelper.attachUnits(pose);
    assertEquals(Tensors.fromString("{6.2[m], 4.2[m], 1}"), poseConv);
  }

  public void testRequire() {
    PoseHelper.require(Tensors.fromString("{6.2[m], 4.2[m], 1}"));
    try {
      PoseHelper.require(Tensors.fromString("{6.2[m], 4.2, 1}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      PoseHelper.require(Tensors.fromString("{6.2[m], 4.2[m], 1, 2}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
