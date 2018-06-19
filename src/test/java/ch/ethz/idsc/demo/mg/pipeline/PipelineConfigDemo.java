// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;

enum PipelineConfigDemo {
  ;
  // for testing
  public static void main(String[] args) throws IOException {
    PipelineConfig test = new PipelineConfig();
    TensorProperties.manifest(UserHome.file("config2.properties"), test);
    // private final PipelineConfig pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
  }
}
