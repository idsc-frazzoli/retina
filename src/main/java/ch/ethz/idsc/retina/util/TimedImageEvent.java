// code by jph
package ch.ethz.idsc.retina.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class TimedImageEvent {
  public final int time;
  public final BufferedImage bufferedImage;

  public TimedImageEvent(int time, BufferedImage bufferedImage) {
    this.time = time;
    this.bufferedImage = bufferedImage;
  }

  public TimedImageEvent(int time, BufferedImage bufferedImage, boolean rotated) {
    this.time = time;
    if (rotated) {
      AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
      tx.translate(-bufferedImage.getWidth(), -bufferedImage.getHeight());
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      this.bufferedImage = op.filter(bufferedImage, null);
    } else {
      this.bufferedImage = bufferedImage;
    }
  }
}
