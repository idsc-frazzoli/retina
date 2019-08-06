// code by ynager, jph
package ch.ethz.idsc.retina.util.io;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** writes images as sequence of png files */
public class PngAnimationWriter implements AnimationWriter {
  private static final int MAX_IMAGES = 10000;
  // ---
  private final File directory;
  private final String format;
  // ---
  private int image_count = -1;

  /** Careful: content of given directory will be deleted!
   * 
   * @param directory
   * @param format
   * @throws Exception */
  public PngAnimationWriter(File directory, String format) throws Exception {
    if (directory.isDirectory())
      DeleteDirectory.of(directory, 1, MAX_IMAGES);
    directory.mkdirs();
    if (!directory.isDirectory())
      throw new RuntimeException("" + directory);
    // ---
    this.directory = directory;
    this.format = format + ".png";
  }

  /** Careful: content of given directory will be deleted!
   * 
   * @param directory
   * @throws Exception */
  public PngAnimationWriter(File directory) throws Exception {
    this(directory, "%06d");
  }

  @Override // from AnimationWriter
  public void append(BufferedImage bufferedImage) throws Exception {
    ImageIO.write(bufferedImage, "png", new File(directory, String.format(format, ++image_count)));
  }

  @Override // from AnimationWriter
  public void append(Tensor tensor) throws Exception {
    append(ImageFormat.of(tensor));
  }

  @Override // from AnimationWriter
  public void close() throws Exception {
    // ---
  }
}
