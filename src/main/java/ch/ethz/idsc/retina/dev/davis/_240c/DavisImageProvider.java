// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.DimensionInterface;
import ch.ethz.idsc.retina.dev.davis.ApsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** for davis240c the raw image data arrives in the order
 * (0,0), (0,1), ..., (0,179), (1,0), (1,1), ..., (239,179) */
public class DavisImageProvider implements ApsDavisEventListener {
  private final int width;
  private final int height;
  private final int lastX;
  private final int lastY;
  // ---
  private final List<ColumnTimedImageListener> timedImageListeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final byte[] bytes;
  private final int[] time;
  private final ApsTracker apsTracker = new ApsTracker();

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

  public void addListener(ColumnTimedImageListener timedImageListener) {
    timedImageListeners.add(timedImageListener);
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    apsTracker.aps(apsDavisEvent, height);
    byte intensity = apsDavisEvent.grayscale();
    int index = apsDavisEvent.x + (apsDavisEvent.y * width); // TODO should precompute?
    bytes[index] = intensity;
    if (apsDavisEvent.y == lastY) {
      time[apsDavisEvent.x] = apsDavisEvent.time;
      if (apsDavisEvent.x == lastX) {
        boolean isComplete = apsTracker.statusAndReset();
        // System.out.println(DeleteDuplicates.of(Differences.of(Tensors.vectorInt(time))));
        timedImageListeners.forEach(listener -> listener.image(time, bufferedImage, isComplete));
      }
    }
  }
}
