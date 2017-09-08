// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import ch.ethz.idsc.retina.util.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** stores last complete to image */
public class DavisImageBuffer implements ColumnTimedImageListener {
  private static final JPanel JPANEL = new JPanel();
  // ---
  private BufferedImage bufferedImage;
  private boolean hasImage = false;

  public DavisImageBuffer() {
    bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
  }

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (!isComplete)
      System.err.println("reset image not complete");
    this.bufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, JPANEL);
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
