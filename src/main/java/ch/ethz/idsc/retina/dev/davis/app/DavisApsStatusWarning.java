// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.util.ColumnTimedImage;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

// TODO implementation seems incomplete
/* package */ class DavisApsStatusWarning implements ColumnTimedImageListener {
  int complete = 0;
  int total = 0;

  @Override // from ColumnTimedImageListener
  public void columnTimedImage(ColumnTimedImage columnTimedImage) {
    ++total;
    if (columnTimedImage.isComplete) {
      ++complete;
      // System.err.println(String.format("complete: %d / %d", complete, total));
    } else {
      // ---
    }
  }
}
