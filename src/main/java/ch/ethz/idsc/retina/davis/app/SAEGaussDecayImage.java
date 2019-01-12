// code by az
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** surface of active events */
public class SAEGaussDecayImage extends AbstractAccumulatedImage {
  public static AbstractAccumulatedImage of(DavisDevice davisDevice, int interval_us) {
    return new SAEGaussDecayImage(davisDevice, interval_us);
  }
  // ---

  /** number of bits to drop */
  private static final int DROP = 7;
  // ---
  private final GaussDecayLookup gaussP;
  private final GaussDecayLookup gaussN;

  private SAEGaussDecayImage(DavisDevice davisDevice, int interval) {
    super(davisDevice);
    setInterval(interval);
    // ---
    gaussP = new GaussDecayLookup(interval >> DROP, 2.5, +1);
    gaussN = new GaussDecayLookup(interval >> DROP, 2.5, -1);
  }

  @Override // from AbstractAccumulatedImage
  protected void assign(int delta, DavisDvsEvent dvsDavisEvent) {
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = dvsDavisEvent.brightToDark() //
        ? gaussN.get(delta >> DROP)
        : gaussP.get(delta >> DROP);
  }
}
