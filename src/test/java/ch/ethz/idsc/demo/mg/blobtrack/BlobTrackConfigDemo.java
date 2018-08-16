// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import java.io.IOException;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;

enum BlobTrackConfigDemo {
  ;
  // for testing
  public static void main(String[] args) throws IOException {
    BlobTrackConfig test = new BlobTrackConfig();
    TensorProperties.manifest(UserHome.file("config2.properties"), test);
    // private final PipelineConfig pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
  }
}
