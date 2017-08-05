// code by jph
package ch.ethz.idsc.retina.dvs.io.png;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.retina.dev.davis._240c.TimedImageListener;

public class SimpleImageWriter implements TimedImageListener {
  private final File directory;
  private final int limit;
  private int count = 0;

  /** @param directory base
   * @param limit
   * @throws Exception */
  public SimpleImageWriter(File directory, int limit) throws Exception {
    this.directory = directory;
    this.limit = limit;
  }

  @Override
  public void image(int time, BufferedImage bufferedImage) {
    if (limit < 0 || count < limit)
      try {
        final String string = String.format("%08d.png", count);
        File file = new File(directory, string);
        ImageIO.write(bufferedImage, "png", file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    ++count;
  }
}
