// code by ynager, jph
package ch.ethz.idsc.retina.util.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

public class PngImageWriter implements Consumer<BufferedImage> {
  private final File folder;
  private final String format;
  // ---
  private int image_count = -1;

  public PngImageWriter(File folder) {
    this(folder, "%06d");
  }

  public PngImageWriter(File folder, String format) {
    this.folder = folder;
    this.format = format + ".png";
  }

  @Override // from Consumer
  public void accept(BufferedImage bufferedImage) {
    try {
      ImageIO.write(bufferedImage, "png", new File(folder, String.format(format, ++image_count)));
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
