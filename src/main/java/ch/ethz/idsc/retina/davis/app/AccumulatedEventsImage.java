// code by jpg
package ch.ethz.idsc.retina.davis.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.core.TimedImageListener;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;

public class AccumulatedEventsImage implements DavisDvsEventListener {
  private static final byte CLEAR_BYTE = (byte) 128;
  // ---
  private final int width;
  private final int height;
  private final List<TimedImageListener> listeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final byte[] bytes;
  private final int interval;
  private Integer last = null;

  /** @param interval [us] */
  public AccumulatedEventsImage(DavisDevice davisDevice, int interval) {
    width = davisDevice.getWidth();
    height = davisDevice.getHeight();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == width * height);
    this.interval = interval;
    // ---
    clearImage();
  }

  public void addListener(TimedImageListener timedImageListener) {
    listeners.add(timedImageListener);
  }

  @Override
  public void dvs(DavisDvsEvent dvsDavisEvent) {
    if (Objects.isNull(last))
      last = dvsDavisEvent.time;
    if (dvsDavisEvent.time < last) { // FIXME find better math criterion
      // System.err.println("clear accumuated events image due to reverse timing");
      clearImage();
      last = dvsDavisEvent.time + interval;
    }
    if (last + interval < dvsDavisEvent.time) {
      listeners.forEach(listener -> listener.image(last, bufferedImage));
      clearImage();
      last += interval;
    }
    int polarity = dvsDavisEvent.i == 0 ? 0 : 255;
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = (byte) polarity;
  }

  private void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }
}
