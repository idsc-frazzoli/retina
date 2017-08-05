// code by jph
package ch.ethz.idsc.retina.dvs.io.png;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import ch.ethz.idsc.retina.dev.davis240c.TimedImageListener;

public class PngImageWriter implements TimedImageListener, AutoCloseable {
  private final File directory;
  private final BufferedWriter bufferedWriter;
  private int count = 0;

  /** @param directory base
   * @throws Exception */
  public PngImageWriter(File directory) throws Exception {
    this.directory = directory;
    File images = new File(directory, "images");
    images.mkdir();
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "images.txt")));
  }

  @Override
  public void image(int time, BufferedImage bufferedImage) {
    try {
      final String string = String.format("images/frame_%08d.png", count);
      bufferedWriter.write(String.format("%.6f %s\n", time * 1e-6, string));
      File file = new File(directory, string);
      ImageIO.write(bufferedImage, "png", file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    ++count;
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }
}
