// code by jpg
package ch.ethz.idsc.retina.davis.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;

/** synthesizes grayscale images based on incoming events during intervals of
 * fixed duration positive events appear in white color negative events appear
 * in black color */
// TODO JPH contains a lot of redundancy
public final class ConstantAccumulatedImage implements DavisDvsListener {
  /** default value 50 ms */
  public static final int INTERVAL_DEFAULT_US = 1_000;
  /** periods without events of length longer than max gap means the timer
   * will skip to the next event position. this is the case when the log file
   * skips to the future. */
  // ---
  private static final byte CLEAR_BYTE = (byte) 128;
  // ---
  protected final int width;
  private final int height;
  private final List<TimedImageListener> listeners = new CopyOnWriteArrayList<>();
  private final BufferedImage bufferedImage;
  protected final byte[] bytes;
  private int count = 0;
  private int interval;
  private Integer last = null;

  public ConstantAccumulatedImage(DavisDevice davisDevice) {
    setInterval(INTERVAL_DEFAULT_US);
    width = davisDevice.getWidth();
    height = davisDevice.getHeight();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == width * height);
    clearImage();
  }

  public final void addListener(TimedImageListener timedImageListener) {
    listeners.add(timedImageListener);
  }

  public final void setInterval(int interval) {
    this.interval = interval;
  }

  public final int getInterval() {
    return interval;
  }

  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent dvsDavisEvent) {
    if (Objects.isNull(last))
      last = dvsDavisEvent.time;
    assign(dvsDavisEvent);
    ++count;
    if (count == interval) {
      TimedImageEvent timedImageEvent = new TimedImageEvent(last, bufferedImage);
      listeners.forEach(listener -> listener.timedImage(timedImageEvent));
      clearImage();
      count = 0;
      last = null;
    }
  }

  protected void assign(DavisDvsEvent dvsDavisEvent) {
    int value = dvsDavisEvent.brightToDark() ? 0 : 255;
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = (byte) value;
  }

  private void clearImage() {
    Arrays.fill(bytes, CLEAR_BYTE);
  }
}
