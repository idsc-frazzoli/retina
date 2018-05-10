// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;

public interface LocalizationImage {
  /** @return image for background visualization */
  BufferedImage getImage();

  /** @return image for scoring */
  BufferedImage getImageExtruded();

  /** @return 3x3 transformation to map model coordinates to pixels */
  Tensor getModel2Pixel();
}
