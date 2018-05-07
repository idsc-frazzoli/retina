package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class PipelineConfig {
  public Scalar threshold = RealScalar.of(0.3);
  public Scalar another = RealScalar.of(4);
   
  
  public static void main(String[] args) throws IOException {
    PipelineConfig test = new PipelineConfig();
    test.another = RealScalar.of(5);
    TensorProperties.manifest(UserHome.file("config.properties"), test);
    
    
  }
}
