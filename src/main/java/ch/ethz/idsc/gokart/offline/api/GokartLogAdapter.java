// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.Tensor;

public class GokartLogAdapter implements GokartLogInterface {
  public static GokartLogInterface of(File folder) {
    return new GokartLogAdapter(folder);
  }

  // ---
  private final File folder;
  private final GokartLogConfig gokartLogConfig;

  private GokartLogAdapter(File folder) {
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
