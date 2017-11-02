// code by az
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/**  */
// TODO spell out abbreviation SAE
public class SAEExpDecayImage extends AbstractAccumulatedImage {
  /** number of bits to drop */
  private static final int DROP = 7;
  // ---
  private final ExpDecayLookup expP;
  private final ExpDecayLookup expN;

  /** @param interval [us] */
  public SAEExpDecayImage(DavisDevice davisDevice, int interval) {
    super(davisDevice, interval);
    // ---
    expP = new ExpDecayLookup(interval >> DROP, 3.0, +1);
    expN = new ExpDecayLookup(interval >> DROP, 3.0, -1);
  }

  @Override
  protected void assign(int delta, DavisDvsEvent dvsDavisEvent) {
    // old implementation:
    // double normts = 1.0 - delta / (double) interval;
    // double scaledts = -3.0 * normts;
    // double decayedts = Math.exp(scaledts);
    // int polarity = dvsDavisEvent.brightToDark() ? -1 : 1;
    // double grayscale = 127.5 * (1 + decayedts * polarity);
    // int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    // bytes[index] = (byte) grayscale;
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = dvsDavisEvent.brightToDark() ? expN.get(delta >> DROP) : expP.get(delta >> DROP);
  }
}
