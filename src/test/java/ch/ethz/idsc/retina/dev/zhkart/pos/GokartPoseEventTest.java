// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartPoseEventTest extends TestCase {
  public void testSimple() {
    Tensor pose = Tensors.fromString("{1[m],2[m],3}");
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(pose);
    assertEquals(gokartPoseEvent.getPose(), pose);
  }

  public void testFail() {
    Tensor pose = Tensors.fromString("{1,2,3}");
    try {
      new GokartPoseEvent(pose);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
