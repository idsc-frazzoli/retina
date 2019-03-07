// code by jpg
package ch.ethz.idsc.retina.davis.app;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** synthesizes grayscale images based on incoming events during intervals of
 * fixed duration positive events appear in white color negative events appear
 * in black color */
public class AccumulatedOverlay implements DavisDvsListener {
  private static final Scalar ALPHA = RealScalar.of(255);
  private static final ScalarUnaryOperator BOUND_ALPHA = Min.function(ALPHA);
  // ---
  private final List<TimedImageListener> listeners = new CopyOnWriteArrayList<>();
  private Tensor background;
  private Tensor collect_P = Array.zeros(180, 240);
  private Tensor collect_N = Array.zeros(180, 240);
  private Tensor alphamask = Array.of(l -> ALPHA, 180, 240);
  private final int interval;
  private Integer last = null;
  private int postpone = 0;
  private int eventCount = 0;
  // ---
  public final ColumnTimedImageListener differenceListener = new ColumnTimedImageListener() {
    @Override
    public void columnTimedImage(ColumnTimedImage columnTimedImage) {
      BufferedImage modif = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
      Graphics graphics = modif.createGraphics();
      graphics.drawImage(columnTimedImage.bufferedImage, 0, 0, null);
      background = ImageFormat.from(modif);
    }
  };

  /** @param interval
   * [us] */
  public AccumulatedOverlay(DavisDevice davisDevice, int interval) {
    this.interval = interval;
    GlobalAssert.that(0 < interval);
  }

  public void addListener(TimedImageListener timedImageListener) {
    listeners.add(timedImageListener);
  }

  @Override
  public void davisDvs(DavisDvsEvent dvsDavisEvent) {
    if (Objects.isNull(last))
      last = dvsDavisEvent.time;
    if (dvsDavisEvent.time - last < 0) {
      System.err.println("dvs image clear due to reverse timing");
      clearImage();
      last = dvsDavisEvent.time;
    } else //
    {
      ++eventCount;
      while (interval + postpone < dvsDavisEvent.time - last) {
        if (Objects.nonNull(background)) {
          Tensor image = Tensors.of( //
              background.add(collect_N).map(BOUND_ALPHA), //
              background.add(collect_P).map(BOUND_ALPHA), //
              background, //
              alphamask);
          image = Transpose.of(image, 2, 0, 1);
          TimedImageEvent timedImageEvent = new TimedImageEvent(last, ImageFormat.of(image));
          listeners.forEach(listener -> listener.timedImage(timedImageEvent));
          System.out.println("overlay -> " + postpone + " " + eventCount);
        }
        clearImage();
        last += interval + postpone;
        postpone = 0;
        eventCount = 0;
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
