// code by jph
package ch.ethz.idsc.retina.util;

import java.awt.image.BufferedImage;

public class TimedImageEvent {
  public final int time;
  public final BufferedImage bufferedImage;

  public TimedImageEvent(int time, BufferedImage bufferedImage) {
    this.time = time;
    this.bufferedImage = bufferedImage;
  }
}
