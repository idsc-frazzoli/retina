// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.owly.data.GlobalAssert;

public class BufferedImageCopy {
  private static final BufferedImage DUMMY = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  // ---
  private BufferedImage copy = null;

  public synchronized void update(BufferedImage bufferedImage) {
    GlobalAssert.that(Objects.nonNull(bufferedImage));
    if (!hasValue())
      copy = new BufferedImage( //
          bufferedImage.getWidth(), //
          bufferedImage.getHeight(), //
          bufferedImage.getType());
    copy.getGraphics().drawImage(bufferedImage, 0, 0, null);
  }

  public boolean hasValue() {
    return Objects.nonNull(copy);
  }

  public BufferedImage get() {
    return hasValue() ? copy : DUMMY;
  }
}
