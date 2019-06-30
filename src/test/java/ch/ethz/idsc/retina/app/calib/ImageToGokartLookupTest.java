// code by jph, mg
package ch.ethz.idsc.retina.app.calib;

import ch.ethz.idsc.demo.mg.MgLogFileLocations;
import ch.ethz.idsc.retina.app.slam.config.EventCamera;
import ch.ethz.idsc.retina.app.slam.config.SlamCoreConfig;
import junit.framework.TestCase;

public class ImageToGokartLookupTest extends TestCase {
  public void testSimple() {
    SlamCoreConfig slamCoreConfig = EventCamera.DAVIS.slamCoreConfig;
    slamCoreConfig.dvsConfig.logFileLocations = MgLogFileLocations.DUBI15a;
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
