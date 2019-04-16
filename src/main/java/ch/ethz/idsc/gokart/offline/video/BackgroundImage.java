// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.Tensor;

public class BackgroundImage {
  /** @param file of image
   * @param model2pixel
   * @return
   * @throws IOException */
  public static BackgroundImage from(File file, Tensor model2pixel) throws IOException {
    return new BackgroundImage(ImageIO.read(file), model2pixel);
  }

  // ---
  public final BufferedImage bufferedImage;
  public final Tensor model2pixel;

  /* package */ BackgroundImage(BufferedImage bufferedImage, Tensor model2pixel) {
    this.bufferedImage = bufferedImage;
    this.model2pixel = model2pixel;
  }

  public Dimension dimension() {
    return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
  }
}
