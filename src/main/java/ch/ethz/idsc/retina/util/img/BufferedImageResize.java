// code by mg
package ch.ethz.idsc.retina.util.img;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public enum BufferedImageResize {
  ;
  /** scales a bufferedImage. if scaled width/height is smaller than 1, it is set to 1
   * 
   * @param bufferedImage original bufferedImage
   * @param scale scaling factor
   * @return scaled bufferedImage */
  public static BufferedImage of(BufferedImage bufferedImage, double scale) {
    int scale_w = (int) Math.max(1, Math.round(scale * bufferedImage.getWidth()));
    int scale_h = (int) Math.max(1, Math.round(scale * bufferedImage.getHeight()));
    AffineTransform affineTransform = AffineTransform.getScaleInstance(scale, scale);
    AffineTransformOp affineTransformOp = //
        new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return affineTransformOp.filter(bufferedImage, new BufferedImage(scale_w, scale_h, bufferedImage.getType()));
  }
}
