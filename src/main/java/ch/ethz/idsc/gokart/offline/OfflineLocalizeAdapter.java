// code by jph
package ch.ethz.idsc.gokart.offline;

import java.io.File;

import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.Tensor;

public class OfflineLocalizeAdapter implements OfflineLocalizeInterface {
  private final File folder;

  public OfflineLocalizeAdapter(File folder) {
    this.folder = folder;
  }

  @Override
  public Tensor model() {
    InitialPose initialPose = TensorProperties.retrieve( //
        new File(folder, "InitialPose.properties"), new InitialPose());
    return initialPose.model();
  }

  @Override
  public File file() {
    return new File(folder, "log.lcm");
  }
}
