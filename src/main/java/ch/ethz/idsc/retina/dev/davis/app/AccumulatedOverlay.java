// code by jpg
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.Min;

/** synthesizes grayscale images based on incoming events during intervals of fixed duration
 * positive events appear in white color
 * negative events appear in black color */
public class AccumulatedOverlay implements DavisDvsEventListener {
  private static final Scalar ALPHA = RealScalar.of(255);
  // ---
  private final List<TimedImageListener> listeners = new LinkedList<>();
  private Tensor background;
  private Tensor collect_P = Array.zeros(180, 240);
  private Tensor collect_N = Array.zeros(180, 240);
  private Tensor alphamask = Array.of(l -> ALPHA, 180, 240);
  private final int interval;
  private Integer last = null;
  private int postpone = 0;
  // ---
  public final ColumnTimedImageListener differenceListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      background = ImageFormat.from(bufferedImage);
    }
  };
  public final ColumnTimedImageListener sig = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      int duration = time[time.length - 1] - time[0];
      // System.out.println("sig " + duration);
      if (isComplete)
        postpone += duration;
      else
        System.err.println("skip");
    }
  };
  public final ColumnTimedImageListener rst = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      int duration = time[time.length - 1] - time[0];
      // System.out.println("rst " + duration);
      if (isComplete)
        postpone += duration;
      else
        System.err.println("skip");
    }
  };

  /** @param interval [us] */
  public AccumulatedOverlay(DavisDevice davisDevice, int interval) {
    this.interval = interval;
    GlobalAssert.that(0 < interval);
  }

  public void addListener(TimedImageListener timedImageListener) {
    listeners.add(timedImageListener);
  }

  @Override
  public void dvs(DavisDvsEvent dvsDavisEvent) {
    if (Objects.isNull(last))
      last = dvsDavisEvent.time;
    final int delta = dvsDavisEvent.time - last;
    if (delta < 0) {
      System.err.println("dvs image clear due to reverse timing");
      clearImage();
      last = dvsDavisEvent.time;
    } else //
    {
      final int delay = interval + postpone;
      if (delay < delta) {
        if (Objects.nonNull(background)) {
          Tensor image = Tensors.of( //
              background.add(collect_N).map(Min.function(ALPHA)), //
              background.add(collect_P).map(Min.function(ALPHA)), //
              background, //
              alphamask);
          image = Transpose.of(image, 2, 0, 1);
          BufferedImage bufferedImage = ImageFormat.of(image);
          listeners.forEach(listener -> listener.image(last, bufferedImage));
          System.out.println("overlay -> " + postpone);
        }
        clearImage();
        last += delay;
        postpone = 0;
        // System.out.println("RESET");
      }
    }
    if (dvsDavisEvent.i == 0) // from bright to dark
      collect_N.set(ALPHA, dvsDavisEvent.y, dvsDavisEvent.x);
    else
      collect_P.set(ALPHA, dvsDavisEvent.y, dvsDavisEvent.x);
  }

  void clearImage() {
    collect_P = collect_P.map(Scalar::zero);
    collect_N = collect_N.map(Scalar::zero);
  }
}
