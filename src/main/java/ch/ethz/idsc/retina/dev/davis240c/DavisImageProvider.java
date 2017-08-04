// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.data.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ImageFormat;

public class DavisImageProvider implements ApsDavisEventListener {
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private static final int LAST_X = WIDTH - 1;
  // ---
  private final List<TimedImageListener> timedImageListeners = new LinkedList<>();
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] bytes;
  private final Tensor image = Array.zeros(WIDTH, HEIGHT);

  public DavisImageProvider() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == WIDTH * HEIGHT);
  }

  public void addListener(TimedImageListener timedImageListener) {
    timedImageListeners.add(timedImageListener);
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    int intensity = apsDavisEvent.grayscale();
    image.set(RealScalar.of(intensity), apsDavisEvent.x, apsDavisEvent.y);
    int index = apsDavisEvent.x + (apsDavisEvent.y * WIDTH); // TODO should precompute?
    bytes[index] = (byte) intensity;
    if (apsDavisEvent.x == LAST_X && apsDavisEvent.y == 0) {
      BufferedImage bi = ImageFormat.of(image);
      timedImageListeners.forEach(listener -> listener.image(apsDavisEvent.time, bi));
      // timedImageListeners.forEach(listener -> listener.image(apsDavisEvent.time, bufferedImage));
    }
  }
}
