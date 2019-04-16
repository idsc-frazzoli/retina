// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

public enum ExtrudedImageCache {
  ;
  /* package */ static final File FOLDER = new File("resources/cache", "extruded");

  /** @param title of image file
   * @param supplier
   * @return */
  public static BufferedImage of(String title, Supplier<BufferedImage> supplier) {
    FOLDER.mkdirs();
    File file = new File(FOLDER, title + ".png");
    if (file.isFile())
      try {
        return ImageIO.read(file);
      } catch (Exception exception) {
        // ---
      }
    BufferedImage bufferedImage = supplier.get();
    try {
      ImageIO.write(bufferedImage, "png", file);
    } catch (Exception exception) {
      // ---
    }
    return bufferedImage;
  }
}
