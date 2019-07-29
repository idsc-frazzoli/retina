// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.UserName;
import junit.framework.TestCase;

public class MgLogFileLocationsTest extends TestCase {
  public void testSimple() {
    assertNotNull(MgLogFileLocations.DUBI12a.calibration());
    assertNotNull(MgLogFileLocations.DUBI14b.calibration());
    assertNotNull(MgLogFileLocations.DUBI15c.calibration());
  }

  public void testDatahaki() {
    if (UserName.is("datahaki")) {
      File file = MgLogFileLocations.DUBI15a.getFile();
      assertTrue(file.isFile());
    }
  }

  public void testResourceData() {
    Tensor inputTensor = ResourceData.of("/demo/mg/DUBI12.csv");
    assertNotNull(inputTensor);
  }
}
