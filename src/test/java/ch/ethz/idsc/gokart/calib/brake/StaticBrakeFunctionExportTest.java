// code by jph
package ch.ethz.idsc.gokart.calib.brake;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class StaticBrakeFunctionExportTest extends TestCase {
  public void testSimple() throws IOException {
    File file = HomeDirectory.file(StaticBrakeFunctionExport.class.getSimpleName() + ".csv");
    assertFalse(file.exists());
    StaticBrakeFunctionExport.to(file);
    assertTrue(file.isFile());
    file.delete();
  }
}
