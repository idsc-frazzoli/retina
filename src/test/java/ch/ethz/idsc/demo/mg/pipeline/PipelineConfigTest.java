// code by jph
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.mg.blobtrack.algo.BlobTrackConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import junit.framework.TestCase;

public class PipelineConfigTest extends TestCase {
  public void testSimple() throws IOException {
    BlobTrackConfig test = new BlobTrackConfig();
    File file = UserHome.file("__" + PipelineConfigTest.class.getSimpleName() + "__.properties");
    TensorProperties.manifest(file, test);
    assertTrue(file.isFile());
    file.delete();
  }
}
