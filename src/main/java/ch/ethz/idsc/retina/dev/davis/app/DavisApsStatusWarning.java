// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

/* package */ class DavisApsStatusWarning implements ColumnTimedImageListener {
  int complete = 0;
  int total = 0;

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    ++total;
    if (isComplete) {
      ++complete;
      // TODO
      // System.err.println(String.format("complete: %d / %d", complete, total));
    } else {
      // ---
    }
  }
}
