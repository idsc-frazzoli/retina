// code by jph, mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;
import junit.framework.TestCase;

public class ImageToWorldUtilTest extends TestCase {
  public void testSimple() {
    ImageToGokartUtil test = new PipelineConfig().createImageToGokartUtil();
    double[] physicalPos = test.imageToGokart(170, 100);
    assertTrue(2 < physicalPos[0]);
    assertTrue(physicalPos[1] < 0);
  }
}
