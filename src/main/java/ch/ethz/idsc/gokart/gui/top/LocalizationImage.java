// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;

public interface LocalizationImage {
  BufferedImage getImage();

  BufferedImage getImageExtruded();

  Tensor getModel2Pixel();
}
