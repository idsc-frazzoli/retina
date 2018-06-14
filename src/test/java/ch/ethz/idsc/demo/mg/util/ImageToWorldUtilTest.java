// code by jph, mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import junit.framework.TestCase;

public class ImageToWorldUtilTest extends TestCase {
  public void testSimple() {
    ImageToWorldUtil test = new PipelineConfig().createTransformUtil();
    double[] physicalPos = test.imageToWorld(170, 100);
    assertTrue(2 < physicalPos[0]);
    assertTrue(physicalPos[1] < 0);
  }
}
