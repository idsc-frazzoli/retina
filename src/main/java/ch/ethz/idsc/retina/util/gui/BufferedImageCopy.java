// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JPanel;

import ch.ethz.idsc.owly.data.GlobalAssert;

public class BufferedImageCopy {
  private static final BufferedImage DUMMY = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  // ---
  private final JPanel JPANEL = new JPanel();
  private BufferedImage copy = null;

  public void update(BufferedImage bufferedImage) {
    GlobalAssert.that(Objects.nonNull(bufferedImage));
    if (!hasValue()) {
      int imageType = bufferedImage.getType();
      System.out.println("imageType=" + imageType);
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      copy = new BufferedImage(width, height, imageType);
    }
    Graphics graphics = copy.getGraphics();
    graphics.drawImage(bufferedImage, 0, 0, JPANEL);
  }

  public boolean hasValue() {
    return Objects.nonNull(copy);
  }

  public BufferedImage get() {
    return hasValue() ? copy : DUMMY;
  }
}
