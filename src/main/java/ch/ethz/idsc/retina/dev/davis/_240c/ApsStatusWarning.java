// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.retina.dev.davis.ColumnTimedImageListener;

public class ApsStatusWarning implements ColumnTimedImageListener {
  int complete = 0;
  int total = 0;

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    ++total;
    if (isComplete) {
      ++complete;
      // TODO
      System.err.println(String.format("complete: %d / %d", complete, total));
    } else {
      // ---
    }
  }
}
