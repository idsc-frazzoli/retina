// code by az
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** surface of active events with intensity calculated using exponential decay */
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
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = dvsDavisEvent.brightToDark() ? expN.get(delta >> DROP) : expP.get(delta >> DROP);
  }
}
