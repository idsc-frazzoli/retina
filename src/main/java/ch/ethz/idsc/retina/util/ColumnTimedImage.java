// code by jph
package ch.ethz.idsc.retina.util;

import java.awt.image.BufferedImage;

public class ColumnTimedImage {
  /** array of timestamps in [us] with time.length == bufferedImage.getWidth() */
  public final int[] time;
  /** grayscale with 8-bit per pixel */
  public final BufferedImage bufferedImage;
  /** whether all information was available in order to create the image */
  public final boolean isComplete;

  public ColumnTimedImage(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    this.time = time;
    this.bufferedImage = bufferedImage;
    this.isComplete = isComplete;
  }

  public int duration() {
    return time[time.length - 1] - time[0]; // TODO 1 missing
  }
}
