// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
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

  @Override // from GokartLogInterface
  public Tensor model() {
    return GokartPoseHelper.toSE2Matrix(gokartLogConfig.pose);
  }
}
