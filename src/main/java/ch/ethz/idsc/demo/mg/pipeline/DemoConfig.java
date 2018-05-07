package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;

enum DemoConfig {
  ;
  public static void main(String[] args) {
   PipelineConfig retrieved = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
   System.out.println(retrieved.another);
  }
}
