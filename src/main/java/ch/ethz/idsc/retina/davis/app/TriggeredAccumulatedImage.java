// code by jpg
package ch.ethz.idsc.retina.davis.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** synthesizes grayscale images based on incoming events during intervals of
 * fixed duration positive events appear in white color negative events appear
 * in black color */
public class TriggeredAccumulatedImage implements DavisDvsListener {
  private static final byte CLEAR_BYTE = (byte) 128;
  // ---
  protected final int width;
  protected final int height;
  private final BufferedImage bufferedImage;
  protected final byte[] bytes;

  public TriggeredAccumulatedImage(DavisDevice davisDevice) {
    width = davisDevice.getWidth();
    height = davisDevice.getHeight();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    clearImage();
  }

  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    int value = davisDvsEvent.brightToDark() ? 0 : 255;
    int index = davisDvsEvent.x + davisDvsEvent.y * width;
    bytes[index] = (byte) value;
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public void clearImage() {
    Arrays.fill(bytes, CLEAR_BYTE);
  }
}
