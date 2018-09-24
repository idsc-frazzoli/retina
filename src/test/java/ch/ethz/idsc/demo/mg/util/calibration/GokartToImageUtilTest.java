// code by jph
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class GokartToImageUtilTest extends TestCase {
  public void testSimple() {
    GokartToImageUtil gokartToImageUtil = GokartToImageUtil.fromMatrix(LogFileLocations.DUBI19q.calibration(), RealScalar.of(1000));
    double[] imgPos = gokartToImageUtil.gokartToImage(3.4386292832405725, -0.4673008409796591);
    assertEquals(imgPos[0], 169.87868054771837, 1e-8);
    assertEquals(imgPos[1], 109.48203883816221, 1e-8);
  }
}
