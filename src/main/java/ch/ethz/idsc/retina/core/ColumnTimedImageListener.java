// code by jph
package ch.ethz.idsc.retina.core;

import java.awt.image.BufferedImage;

/** the aps images are read column wise from left to right.
 * each column is assigned a separate timestamp in [us].
 * typically the intervals between the timestamps are quite regular. */
public interface ColumnTimedImageListener {
  /** @param time array of timestamps in [us] with time.length == bufferedImage.getWidth()
   * @param bufferedImage grayscale with 8-bit per pixel
   * @param isComplete */
  void image(int[] time, BufferedImage bufferedImage, boolean isComplete);
}
