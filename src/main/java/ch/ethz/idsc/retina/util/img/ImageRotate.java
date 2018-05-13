// code by mg
package ch.ethz.idsc.retina.util.img;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageRotate.html">ImageRotate</a> */
public enum ImageRotate {
  ;
  /** @param bufferedImage
   * @return new image that is the old one rotated by 180[deg] */
  public static BufferedImage _180deg(BufferedImage bufferedImage) {
    AffineTransform affineTransform = AffineTransform.getScaleInstance(-1, -1);
    affineTransform.translate(-bufferedImage.getWidth(), -bufferedImage.getHeight());
    AffineTransformOp affineTransformOp = //
        new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return affineTransformOp.filter(bufferedImage, null);
  }
}
