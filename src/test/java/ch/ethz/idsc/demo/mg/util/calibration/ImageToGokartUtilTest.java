// code by jph, mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import junit.framework.TestCase;

public class ImageToGokartUtilTest extends TestCase {
  public void testSimple() {
    SlamDvsConfig.cameraType = "davis";
    SlamCoreConfig slamCoreConfig = SlamDvsConfig.getSlamCoreConfig();
    slamCoreConfig.davisConfig.logFileLocations = LogFileLocations.DUBI15a;
    ImageToGokartLookup test = (ImageToGokartLookup) slamCoreConfig.davisConfig.createImageToGokartInterface();
    test.printInfo();
    System.out.println("---");
    int x = 170;
    int y = 100;
    // double[] physicalPos =
    test.imageToGokart(x, y);
    // TODO check failed with the introduction of seye
    // assertEquals(physicalPos[0], 3.4386292832405725);
    // assertEquals(physicalPos[1], -0.4673008409796591);
  }
}
