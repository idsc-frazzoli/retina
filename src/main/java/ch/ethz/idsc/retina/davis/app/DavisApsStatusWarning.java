// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;

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
