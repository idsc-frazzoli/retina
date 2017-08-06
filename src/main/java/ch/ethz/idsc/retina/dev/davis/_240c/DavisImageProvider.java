// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.DimensionInterface;
import ch.ethz.idsc.retina.dev.davis.ApsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis.TimedImageListener;
import ch.ethz.idsc.retina.util.data.GlobalAssert;

public class DavisImageProvider implements ApsDavisEventListener {
  private final int width;
  private final int height;
  private final int lastX;
  private final int lastY;
  // ---
  private final List<TimedImageListener> timedImageListeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final byte[] bytes;
  // private final Tensor image = Array.zeros(WIDTH, HEIGHT);

  public DavisImageProvider(DimensionInterface dimensionInterface) {
    width = dimensionInterface.getWidth();
    height = dimensionInterface.getHeight();
    lastX = width - 1;
    lastY = height - 1;
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == width * height);
  }

  public void addListener(TimedImageListener timedImageListener) {
    timedImageListeners.add(timedImageListener);
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    int intensity = apsDavisEvent.grayscale();
    // image.set(RealScalar.of(intensity), apsDavisEvent.x, apsDavisEvent.y);
    int index = apsDavisEvent.x + (apsDavisEvent.y * width); // TODO should precompute?
    bytes[index] = (byte) intensity;
    // System.out.println(apsDavisEvent.x +" "+ apsDavisEvent.y);
    if (apsDavisEvent.x == lastX && apsDavisEvent.y == lastY) {
      // BufferedImage bi = ImageFormat.of(image);
      // timedImageListeners.forEach(listener -> listener.image(apsDavisEvent.time, bi));
      timedImageListeners.forEach(listener -> listener.image(apsDavisEvent.time, bufferedImage));
    }
  }
}
