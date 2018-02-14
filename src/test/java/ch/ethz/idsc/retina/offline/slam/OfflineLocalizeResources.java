// code by jph
package ch.ethz.idsc.retina.offline.slam;

import java.io.File;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum OfflineLocalizeResources implements OfflineLocalizeResource {
  TEST(new File("src/test/resources/localization", "vlp16.center.ray_autobox.rimo.get.lcm"), //
      Tensors.matrixDouble(new double[][] { //
          { -6.77422, 3.21868, 422.04915 }, //
          { +3.21868, 6.77422, 213.03233 }, //
          { 0, 0, 1 } }));
  // ---
  private final File file;
  private final Tensor model2pixel;

  private OfflineLocalizeResources(File file, Tensor model2pixel) {
    this.file = file;
    this.model2pixel = model2pixel.unmodifiable();
  }

  @Override
  public File file() {
    return file;
  }

  @Override
  public Tensor model2pixel() {
    return model2pixel;
  }
}
