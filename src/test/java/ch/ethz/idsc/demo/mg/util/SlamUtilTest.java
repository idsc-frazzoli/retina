// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SlamUtilTest extends TestCase {
  // test the go kart to world frame transformation
  public void testGokartToWorld() {
    Tensor testPose = Tensors.of(Quantity.of(2, SI.METER), Quantity.of(1, SI.METER), DoubleScalar.of(Math.PI));
    double[] gokartFramePos = new double[] { 2, 1 };
    Tensor worldCoord = SlamUtil.gokartToWorldTensor(testPose, gokartFramePos);
    System.out.println(worldCoord);
  }

  public static void main(String[] args) {
    SlamUtilTest testing = new SlamUtilTest();
    testing.testGokartToWorld();
  }
}
