// code by jph
package ch.ethz.idsc.retina.util.img;

import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.owl.data.GlobalAssert;

public class ImageCopy {
  private static final BufferedImage DUMMY = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);

  public static BufferedImage of(BufferedImage bufferedImage) {
    ImageCopy imageCopy = new ImageCopy();
    imageCopy.update(bufferedImage);
    return imageCopy.get();
  }

  // ---
  private BufferedImage copy = null;

  public synchronized void update(BufferedImage bufferedImage) {
    GlobalAssert.that(Objects.nonNull(bufferedImage));
    if (!hasValue())
      copy = new BufferedImage( //
          bufferedImage.getWidth(), //
          bufferedImage.getHeight(), //
          bufferedImage.getType());
    copy.createGraphics().drawImage(bufferedImage, 0, 0, null);
  }

  public boolean hasValue() {
    return Objects.nonNull(copy);
  }

  public BufferedImage get() {
    return hasValue() ? copy : DUMMY;
  }
}
