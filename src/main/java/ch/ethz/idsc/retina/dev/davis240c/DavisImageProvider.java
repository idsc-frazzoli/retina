// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.data.GlobalAssert;

public class DavisImageProvider implements ApsDavisEventListener {
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private static final int ADC_MAX = 1023;
  private static final int LAST_X = WIDTH - 1;
  private static final int LAST_Y = HEIGHT - 1;
  // ---
  private final List<DavisImageListener> davisImageListeners = new LinkedList<>();
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] bytes;

  public DavisImageProvider() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == WIDTH * HEIGHT);
  }

  public void addListener(DavisImageListener davisImageListener) {
    davisImageListeners.add(davisImageListener);
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    int index = apsDavisEvent.x + ((LAST_Y - apsDavisEvent.y) * WIDTH); // TODO should precompute?
    bytes[index] = (byte) ((ADC_MAX - apsDavisEvent.adc) >> 2);
    if (apsDavisEvent.x == LAST_X && apsDavisEvent.y == 0)
      davisImageListeners.forEach(l -> l.image(apsDavisEvent.time, bufferedImage));
  }
}
