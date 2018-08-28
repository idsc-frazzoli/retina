// code by jph, mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import junit.framework.TestCase;

public class ImageToGokartUtilTest extends TestCase {
  public void testSimple() {
    SlamConfig slamConfig = new SlamConfig();
    slamConfig.davisConfig.logFileLocations = LogFileLocations.DUBI15a;
    ImageToGokartLookup test = (ImageToGokartLookup) slamConfig.davisConfig.createImageToGokartInterface();
    test.printInfo();
    System.out.println("---");
    int x = 170;
    int y = 100;
    double[] physicalPos = test.imageToGokart(x, y);
    assertEquals(physicalPos[0], 3.4386292832405725);
    assertEquals(physicalPos[1], -0.4673008409796591);
  }
}
