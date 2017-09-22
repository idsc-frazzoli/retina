// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owly.data.Stopwatch;
import ch.ethz.idsc.retina.util.ColumnTimedImage;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.Max;

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
      // TODO long term: use java buffers
      Stopwatch stopwatch = Stopwatch.started();
      Tensor sig = ImageFormat.from(columnTimedImage.bufferedImage);
      Tensor rst = ImageFormat.from(davisImageBuffer.bufferedImage());
      BufferedImage difference = ImageFormat.of(sig.subtract(rst).map(Max.function(RealScalar.ZERO)));
      System.out.println("DIFF in " + stopwatch.display_seconds());
      ColumnTimedImage image = //
          new ColumnTimedImage(columnTimedImage.time, difference, columnTimedImage.isComplete);
      listeners.forEach(listener -> listener.columnTimedImage(image));
    }
  }
}
