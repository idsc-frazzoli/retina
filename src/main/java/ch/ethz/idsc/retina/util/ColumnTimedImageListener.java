// code by jph
package ch.ethz.idsc.retina.util;

import java.awt.image.BufferedImage;

/** the aps images are read column wise from left to right.
 * each column is assigned a separate timestamp in [us].
 * typically the intervals between the timestamps are quite regular.
 * 
 * For the Davis240c the total duration of an image transmission
 * both reset, or signal is 18952[us]. */
public interface ColumnTimedImageListener {
  /** the instance bufferedImage of the provided image is altered in between
   * calls to image(). Therefore the implementation of {@link ColumnTimedImageListener}
   * is required to make a copy of the image for long term use.
   * 
   * @param time array of timestamps in [us] with time.length == bufferedImage.getWidth()
   * @param bufferedImage grayscale with 8-bit per pixel
   * @param isComplete */
  void image(int[] time, BufferedImage bufferedImage, boolean isComplete);
}
