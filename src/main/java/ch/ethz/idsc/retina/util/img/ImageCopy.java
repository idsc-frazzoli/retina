// code by jph
package ch.ethz.idsc.retina.util.img;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
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

  public void update(BufferedImage bufferedImage) {
    GlobalAssert.that(Objects.nonNull(bufferedImage));
    if (!hasValue())
      copy = new BufferedImage( //
          bufferedImage.getWidth(), //
          bufferedImage.getHeight(), //
          bufferedImage.getType());
    // TODO JPH this doesn't work for images with transparency
    copy.createGraphics().drawImage(bufferedImage, 0, 0, null);
  }

  public boolean hasValue() {
    return Objects.nonNull(copy);
  }

  public BufferedImage get() {
    return hasValue() ? copy : DUMMY;
  }

  // https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
  static BufferedImage deepCopy(BufferedImage bufferedImage) {
    ColorModel colorModel = bufferedImage.getColorModel();
    boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
    WritableRaster writableRaster = bufferedImage.copyData(null);
    return new BufferedImage(colorModel, writableRaster, isAlphaPremultiplied, null);
  }
}
