// code by jpg
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** synthesizes grayscale images based on incoming events during intervals of
 * fixed duration positive events appear in white color negative events appear
 * in black color */
public class AccumulatedEventsGrayImage extends AbstractAccumulatedImage {
  /** @param interval [us] */
  public AccumulatedEventsGrayImage(DavisDevice davisDevice, int interval) {
    super(davisDevice, interval);
  }

  @Override
  protected void assign(int delta, DavisDvsEvent dvsDavisEvent) {
    int value = dvsDavisEvent.brightToDark() ? 0 : 255;
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = (byte) value;
  }
}
