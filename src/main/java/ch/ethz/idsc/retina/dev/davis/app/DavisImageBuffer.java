// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.retina.util.ColumnTimedImage;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** stores last complete to image */
// TODO use image copy instead!!!
@Deprecated
public class DavisImageBuffer implements ColumnTimedImageListener {
  private BufferedImage bufferedImage;
  private boolean hasImage = false;

  public DavisImageBuffer() {
    bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
  }

  @Override
  public void image(ColumnTimedImage columnTimedImage) {
    if (!columnTimedImage.isComplete)
      System.err.println("reset image not complete");
    bufferedImage.getGraphics().drawImage(columnTimedImage.bufferedImage, 0, 0, null);
    hasImage = true;
  }

  public boolean hasImage() {
    return hasImage;
  }

  public BufferedImage bufferedImage() {
    GlobalAssert.that(hasImage);
    return bufferedImage;
  }
}
