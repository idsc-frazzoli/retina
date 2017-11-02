// code by jpg
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/**  */
public class SAEExpDecayImage extends AbstractAccumulatedImage {
  /** @param interval [us] */
  public SAEExpDecayImage(DavisDevice davisDevice, int interval) {
    super(davisDevice, interval);
  }

  @Override
  protected void assign(int delta, DavisDvsEvent dvsDavisEvent) {
    double normts = 1.0 - delta / (double) interval;
    double scaledts = -3.0 * normts;
    double decayedts = Math.exp(scaledts);
    int polarity = dvsDavisEvent.brightToDark() ? -1 : 1;
    double grayscale = 127.5 * (1 + decayedts * polarity);
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = (byte) grayscale;
  }
}
