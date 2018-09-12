// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TensorProperties;

public class GokartLogAdapter implements GokartLogInterface {
  public static GokartLogInterface of(File folder) {
    return new GokartLogAdapter(folder);
  }

  // ---
  private final File folder;
  private final GokartLogConfig gokartLogConfig = new GokartLogConfig();

  private GokartLogAdapter(File folder) {
    this.folder = folder;
    TensorProperties.wrap(gokartLogConfig) //
        .tryLoad(new File(folder, "GokartLogConfig.properties"));
  }

  @Override // from GokartLogInterface
  public File file() {
    return new File(folder, "log.lcm");
  }

  @Override // from GokartLogInterface
  public String driver() {
    return gokartLogConfig.driver;
  }

  @Override // from GokartLogInterface
  public Tensor pose() {
    return gokartLogConfig.pose.unmodifiable();
  }
}
