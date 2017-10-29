// code by jpg
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** synthesizes grayscale images based on incoming events during intervals of
 * fixed duration positive events appear in white color negative events appear
 * in black color */
@Deprecated
class AccumulatedEventsRgbaImage implements DavisDvsListener {
  // FIXME implementation is mal-functional
  // private static final int CLEAR_BYTE = 0;
  // ---
  private final int width;
  private final int height;
  private final List<TimedImageListener> listeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final int[] bytes;
  private final int interval;
  private Integer last = null;
  private final Tensor tensor;

  /** @param interval [us] */
  public AccumulatedEventsRgbaImage(DavisDevice davisDevice, int interval) {
    width = davisDevice.getWidth();
    height = davisDevice.getHeight();
    tensor = Array.zeros(height, width, 4);
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    DataBufferInt dataBufferByte = (DataBufferInt) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == width * height);
    this.interval = interval;
    GlobalAssert.that(0 < interval);
    // ---
    clearImage();
  }

  public void addListener(TimedImageListener timedImageListener) {
    listeners.add(timedImageListener);
  }

  @Override
  public void davisDvs(DavisDvsEvent dvsDavisEvent) {
    if (Objects.isNull(last))
      last = dvsDavisEvent.time;
    final int delta = dvsDavisEvent.time - last;
    if (delta < 0) {
      System.err.println("dvs image clear due to reverse timing");
      clearImage();
      last = dvsDavisEvent.time;
    } else //
    if (interval < delta) {
      BufferedImage bi = ImageFormat.of(tensor);
      TimedImageEvent timedImageEvent = new TimedImageEvent(last, bi);
      listeners.forEach(listener -> listener.timedImage(timedImageEvent));
      clearImage();
      last += interval;
    }
    // int polarity = dvsDavisEvent.i == 0 ? 0 : 255;
    // int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    // bytes[index] = ColorFormat.toInt(Tensors.vector(dvsDavisEvent.darkToBright() ? 255 : 0, dvsDavisEvent.darkToBright() ? 0 : 255, 0, 255));
    tensor.set(RealScalar.of(255), dvsDavisEvent.y, dvsDavisEvent.x, 3);
  }

  private void clearImage() {
    tensor.map(scalar -> RealScalar.ZERO);
    // System.out.println("clear " + bytes.length);
    // Random random = new Random();
    // IntStream.range(0, bytes.length).forEach(i -> bytes[i] = random.nextInt() & 0xffffffff);
    // IntStream.range(0, bytes.length).forEach(i -> bytes[i] = 0xff000000);
    // IntStream.range(0, bytes.length).forEach(i -> bytes[i] = 0x00000000);
  }
}
