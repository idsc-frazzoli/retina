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
   * @throws IOException if file does not exist */
  public static BackgroundImage from(File file, Tensor model2pixel) throws IOException {
    BufferedImage bufferedImage = ImageIO.read(file);
    FadeTop.of(bufferedImage);
    return new BackgroundImage(bufferedImage, model2pixel);
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Tensor model2pixel;

  public BackgroundImage(BufferedImage bufferedImage, Tensor model2pixel) {
    this.bufferedImage = bufferedImage;
    this.model2pixel = model2pixel;
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public Tensor model2pixel() {
    return model2pixel;
  }

  public Dimension dimension() {
    return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
  }
}
