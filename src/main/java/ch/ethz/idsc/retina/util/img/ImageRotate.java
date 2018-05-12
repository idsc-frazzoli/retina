// code by mg
package ch.ethz.idsc.retina.util.img;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public enum ImageRotate {
  ;
  public static BufferedImage rotate180Degrees(BufferedImage bufferedImage) {
    AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
    tx.translate(-bufferedImage.getWidth(), -bufferedImage.getHeight());
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    bufferedImage = op.filter(bufferedImage, null);
    return bufferedImage;
  }
}
