// code by jph
package ch.ethz.idsc.retina.davis;

import java.awt.image.BufferedImage;

public interface ColumnTimedImageListener {
  void image(int[] time, BufferedImage bufferedImage, boolean isComplete);
}
