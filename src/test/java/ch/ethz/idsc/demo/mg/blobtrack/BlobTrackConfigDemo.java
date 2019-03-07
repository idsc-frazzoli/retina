// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TensorProperties;

enum BlobTrackConfigDemo {
  ;
  // for testing
  public static void main(String[] args) throws IOException {
    BlobTrackConfig test = new BlobTrackConfig();
    TensorProperties.wrap(test).save(HomeDirectory.file("config2.properties"));
    // private final PipelineConfig pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
  }
}
