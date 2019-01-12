// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.davis.DavisApsListener;
import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.DimensionInterface;

/** for davis240c the raw image data arrives in the order (0,0), (0,1), ...,
 * (0,179), (1,0), (1,1), ..., (239,179) */
public class DavisImageProvider implements DavisApsListener {
  private final int width;
  private final int height;
  private final int lastX;
  private final int lastY;
  // ---
  private final List<ColumnTimedImageListener> listeners = new CopyOnWriteArrayList<>();
  private final BufferedImage bufferedImage;
  private final byte[] bytes;
  private final int[] time;
  private final DavisApsTracker apsTracker = new DavisApsTracker();

  public DavisImageProvider(DimensionInterface dimensionInterface) {
    width = dimensionInterface.getWidth();
    height = dimensionInterface.getHeight();
    lastX = width - 1;
    lastY = height - 1;
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == width * height);
    time = new int[width];
  }

  public void addListener(ColumnTimedImageListener columnTimedImageListener) {
    listeners.add(columnTimedImageListener);
  }

  @Override
  public void davisAps(DavisApsEvent davisApsEvent) {
    apsTracker.aps(davisApsEvent, height);
    byte intensity = davisApsEvent.grayscale();
    int index = davisApsEvent.x + (davisApsEvent.y * width); // TODO should precompute?
    bytes[index] = intensity;
    if (davisApsEvent.y == lastY) {
      time[davisApsEvent.x] = davisApsEvent.time;
      if (davisApsEvent.x == lastX) {
        boolean isComplete = apsTracker.statusAndReset();
        // System.out.println(DeleteDuplicates.of(Differences.of(Tensors.vectorInt(time))));
        ColumnTimedImage columnTimedImage = new ColumnTimedImage(time, bufferedImage, isComplete);
        listeners.forEach(listener -> listener.columnTimedImage(columnTimedImage));
      }
    }
  }
}
