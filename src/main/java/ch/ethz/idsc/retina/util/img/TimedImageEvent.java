// code by jph, mg
package ch.ethz.idsc.retina.util.img;

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
    this.bufferedImage = rotated //
        ? ImageRotate._180deg(bufferedImage)
        : bufferedImage;
  }
}
