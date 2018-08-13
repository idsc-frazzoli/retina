// code by jph, mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import junit.framework.TestCase;

public class ImageToGokartUtilTest extends TestCase {
  public void testSimple() {
    ImageToGokartUtil test = new PipelineConfig().createImageToGokartUtil();
    double[] physicalPos = test.imageToGokart(170, 100);
    assertTrue(2 < physicalPos[0]);
    assertTrue(physicalPos[1] < 0);
  }
}
