// code by mg
package ch.ethz.idsc.retina.util.img;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageReflect.html">ImageReflect</a> */
public enum ImageReflect {
  ;
  /** flips the bufferedImage along the horizontal axis
   * 
   * @param bufferedImage
   * @return flipped bufferedImage */
  public static BufferedImage flipHorizontal(BufferedImage bufferedImage) {
    AffineTransform affineTransform = AffineTransform.getScaleInstance(1, -1);
    affineTransform.translate(0, -bufferedImage.getHeight());
    AffineTransformOp affineTransformOp = //
        new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return affineTransformOp.filter(bufferedImage, null);
  }
}
