// code by az
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** surface of active events with intensity calculated using exponential decay */
public class SAEExpDecayImage extends AbstractAccumulatedImage {
  /** number of bits to drop */
  private static final int DROP = 7;
  // ---
  private final ExpDecayLookup expP;
  private final ExpDecayLookup expN;

  public SAEExpDecayImage(DavisDevice davisDevice, int interval) {
    super(davisDevice);
    setInterval(interval);
    // ---
    expP = new ExpDecayLookup(interval >> DROP, 3.0, +1);
    expN = new ExpDecayLookup(interval >> DROP, 3.0, -1);
  }

  @Override // from AbstractAccumulatedImage
  protected void assign(int delta, DavisDvsEvent dvsDavisEvent) {
    int index = dvsDavisEvent.x + dvsDavisEvent.y * width;
    bytes[index] = dvsDavisEvent.brightToDark() //
        ? expN.get(delta >> DROP)
        : expP.get(delta >> DROP);
  }
}
