// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.Tensor;

public class GokartLogAdapter implements GokartLogInterface {
  private final File folder;
  private final GokartLogConfig gokartLogConfig;

  public GokartLogAdapter(File folder) {
    this.folder = folder;
    gokartLogConfig = TensorProperties.retrieve( //
        new File(folder, "GokartLogConfig.properties"), new GokartLogConfig());
  }

  @Override
  public Tensor model() {
    return gokartLogConfig.model();
  }

  @Override
  public File file() {
    return new File(folder, "log.lcm");
  }
}
