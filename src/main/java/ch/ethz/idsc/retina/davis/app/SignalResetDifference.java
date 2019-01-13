// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.ImageDifference;

/** listens to signal images from which the class subtracts the last reset image */
public class SignalResetDifference implements ColumnTimedImageListener {
  public static SignalResetDifference normal(DavisImageBuffer davisImageBuffer) {
    return new SignalResetDifference(davisImageBuffer, ImageDifference::of);
  }

  public static SignalResetDifference amplified(DavisImageBuffer davisImageBuffer) {
    return new SignalResetDifference(davisImageBuffer, ImageDifference::amplified);
  }

  // ---
  /** buffer for the last reset image */
  private final DavisImageBuffer davisImageBuffer;
  private final BinaryOperator<BufferedImage> binaryOperator;
  private final List<ColumnTimedImageListener> listeners = new CopyOnWriteArrayList<>();

  private SignalResetDifference( //
      DavisImageBuffer davisImageBuffer, BinaryOperator<BufferedImage> binaryOperator) {
    this.davisImageBuffer = davisImageBuffer;
    this.binaryOperator = binaryOperator;
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
          binaryOperator.apply(columnTimedImage.bufferedImage, davisImageBuffer.bufferedImage()), //
          columnTimedImage.isComplete);
      // System.out.println("DIFF in " + stopwatch.display_seconds());
      listeners.forEach(listener -> listener.columnTimedImage(image));
    }
  }
}
