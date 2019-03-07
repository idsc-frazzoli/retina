// code by jph
package ch.ethz.idsc.demo.mg.blobtrack;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TensorProperties;
import junit.framework.TestCase;

public class BlobTrackConfigTest extends TestCase {
  public void testSimple() throws IOException {
    BlobTrackConfig test = new BlobTrackConfig();
    File file = HomeDirectory.file("__" + BlobTrackConfigTest.class.getSimpleName() + "__.properties");
    TensorProperties.wrap(test).save(file);
    assertTrue(file.isFile());
    file.delete();
    assertFalse(file.exists());
  }
}
