// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;

public interface ScatterImage {
  void render(Tensor model_dot_lidar, Tensor points);

  BufferedImage getImage();
}
