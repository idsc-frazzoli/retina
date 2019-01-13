// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.ImageCopy;

/** stores last complete to image */
public class DavisImageBuffer implements ColumnTimedImageListener {
  private final ImageCopy imageCopy = new ImageCopy();

  @Override
  public void columnTimedImage(ColumnTimedImage columnTimedImage) {
    if (!columnTimedImage.isComplete)
      System.err.println("reset image not complete");
    imageCopy.update(columnTimedImage.bufferedImage);
  }

  public boolean hasImage() {
    return imageCopy.hasValue();
  }

  public BufferedImage bufferedImage() {
    GlobalAssert.that(imageCopy.hasValue());
    return imageCopy.get();
  }
}
