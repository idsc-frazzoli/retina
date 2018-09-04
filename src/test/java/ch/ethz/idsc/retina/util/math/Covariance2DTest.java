// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class Covariance2DTest extends TestCase {
  public void testSimple() {
    Covariance2D covariance2d = new Covariance2D(new double[][] { { 3, 0.5 }, { 0.5, 2 } });
    double rotAngle = covariance2d.rotAngle();
    assertEquals(rotAngle, 0.3926990816987242, 1e-10);
    Tensor stdDev = covariance2d.stdDev();
    assertEquals(stdDev.Get(0).number().floatValue(), 1.7908397, 1e-6);
    assertEquals(stdDev.Get(1).number().floatValue(), 1.3389896, 1e-6);
  }
}
