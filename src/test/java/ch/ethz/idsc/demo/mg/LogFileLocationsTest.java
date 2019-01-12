// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.tensor.io.UserName;
import junit.framework.TestCase;

public class LogFileLocationsTest extends TestCase {
  public void testSimple() {
    assertNotNull(LogFileLocations.DUBI12a.calibration());
    assertNotNull(LogFileLocations.DUBI14b.calibration());
    assertNotNull(LogFileLocations.DUBI15c.calibration());
  }

  public void testDatahaki() {
    if (UserName.is("datahaki")) {
      File file = LogFileLocations.DUBI15a.getFile();
      assertTrue(file.isFile());
    }
  }
}
