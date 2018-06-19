// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;

public interface ScatterImage {
  /** @param model_dot_lidar
   * @param points */
  void render(Tensor model_dot_lidar, Tensor points);

  /** @return scatter image, may be null */
  BufferedImage getImage();
}
