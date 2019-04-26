// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2PoseConverterTest extends TestCase {
  public void testPose() {
    Tensor poseMeter = Tensors.fromString("{6.2[m],4.2[m],1}");
    Tensor pose = Tensors.fromString("{6.2, 4.2, 1}");
    Tensor poseConv = GokartPoseHelper.attachUnits(pose);
    assertEquals(poseMeter, poseConv);
  }
}
