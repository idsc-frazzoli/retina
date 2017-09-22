// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.util.ColumnTimedImage;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

/* package */ class DavisApsStatusWarning implements ColumnTimedImageListener {
  int complete = 0;
  int total = 0;

  @Override
  public void image(ColumnTimedImage columnTimedImage) {
    ++total;
    if (columnTimedImage.isComplete) {
      ++complete;
      // TODO
      // System.err.println(String.format("complete: %d / %d", complete, total));
    } else {
      // ---
    }
  }
}
