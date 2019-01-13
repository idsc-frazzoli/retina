// code by jpg
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** synthesizes grayscale images based on incoming events during intervals of
 * fixed duration positive events appear in white color negative events appear
 * in black color */
public class AccumulatedEventsGrayImage extends AbstractAccumulatedImage {
  public static AbstractAccumulatedImage of(DavisDevice davisDevice) {
    return new AccumulatedEventsGrayImage(davisDevice);
  }

  // ---
  private AccumulatedEventsGrayImage(DavisDevice davisDevice) {
    super(davisDevice);
  }

  @Override // from AbstractAccumulatedImage
  protected void assign(int delta, DavisDvsEvent davisDvsEvent) {
    int value = davisDvsEvent.brightToDark() ? 0 : 255;
    int index = davisDvsEvent.x + davisDvsEvent.y * width;
    bytes[index] = (byte) value;
  }
}
