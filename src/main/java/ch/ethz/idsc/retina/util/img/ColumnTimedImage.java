// code by jph
package ch.ethz.idsc.retina.util.img;

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

  /** @return difference between last and first column timestamp */
  public int duration() {
    // formula also works when time.length == 1
    return time[time.length - 1] - time[0];
  }
}
