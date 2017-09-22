// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.ColumnTimedImage;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.ImageDifference;

/** listens to signal images from which the class subtracts the last reset image */
public class SignalResetDifference implements ColumnTimedImageListener {
  /** buffer for the last reset image */
  private final DavisImageBuffer davisImageBuffer;
  private final List<ColumnTimedImageListener> listeners = new LinkedList<>();

  public SignalResetDifference(DavisImageBuffer davisImageBuffer) {
    this.davisImageBuffer = davisImageBuffer;
  }

  public void addListener(ColumnTimedImageListener columnTimedImageListener) {
    listeners.add(columnTimedImageListener);
  }

  @Override
  public void columnTimedImage(ColumnTimedImage columnTimedImage) {
    if (davisImageBuffer.hasImage()) {
      // Stopwatch stopwatch = Stopwatch.started();
      ColumnTimedImage image = new ColumnTimedImage( //
          columnTimedImage.time, //
          ImageDifference.of(columnTimedImage.bufferedImage, davisImageBuffer.bufferedImage()), //
          columnTimedImage.isComplete);
      // System.out.println("DIFF in " + stopwatch.display_seconds());
      listeners.forEach(listener -> listener.columnTimedImage(image));
    }
  }
}
