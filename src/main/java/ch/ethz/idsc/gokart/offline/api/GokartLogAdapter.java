// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TensorProperties;

public class GokartLogAdapter implements GokartLogInterface {
  public static GokartLogInterface of(File folder, String name) {
    return new GokartLogAdapter(folder, name);
  }

  public static GokartLogInterface of(File folder) {
    return of(folder, "log.lcm");
  }

  // ---
  private final File folder;
  private final String name;
  private final GokartLogConfig gokartLogConfig = new GokartLogConfig();

  private GokartLogAdapter(File folder, String name) {
    this.folder = folder;
    this.name = name;
    File file = new File(folder, GokartLogConfig.class.getSimpleName() + ".properties");
    if (!file.isFile())
      System.err.println("warning: missing properties file");
    TensorProperties.wrap(gokartLogConfig).tryLoad(file);
  }

  @Override // from GokartLogInterface
  public File file() {
    return new File(folder, name);
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
