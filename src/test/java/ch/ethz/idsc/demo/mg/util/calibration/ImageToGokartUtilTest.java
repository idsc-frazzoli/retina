// code by jph, mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.demo.mg.slam.config.EventCamera;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import junit.framework.TestCase;

public class ImageToGokartUtilTest extends TestCase {
  public void testSimple() {
    SlamCoreConfig slamCoreConfig = EventCamera.DAVIS.slamCoreConfig;
    slamCoreConfig.dvsConfig.logFileLocations = LogFileLocations.DUBI15a;
    ImageToGokartLookup imageToGokartLookup = //
        (ImageToGokartLookup) slamCoreConfig.dvsConfig.createImageToGokartInterface();
    // imageToGokartLookup.printInfo();
    int x = 170;
    int y = 100;
    double[] physicalPos = imageToGokartLookup.imageToGokart(x, y);
    assertEquals(physicalPos[0], 3.4386292832405725);
    assertEquals(physicalPos[1], -0.4673008409796591);
  }
}
