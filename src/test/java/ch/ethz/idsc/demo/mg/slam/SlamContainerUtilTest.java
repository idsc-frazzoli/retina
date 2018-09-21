// code by jph
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SlamContainerUtilTest extends TestCase {
  public void testSimple() {
    SlamParticle[] slamParticles = SlamParticles.allocate(300);
    Tensor pose = Tensors.fromString("{3.4[m], 5.6[m], 3.345}");
    SlamCoreContainerUtil.setInitialDistribution(slamParticles, pose, 2, 3, 3);
    for (int index = 0; index < 10; ++index) {
      Scalar angVel = slamParticles[index].getAngVel();
      System.out.println(angVel);
    }
  }
}
