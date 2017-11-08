// code by az
package ch.ethz.idsc.retina.dev.davis.app;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/**  */
// TODO spell out abbreviation SAE
public class SAEGausDecayImage extends AbstractAccumulatedImage {
  /** number of bits to drop */
  private static final int DROP = 7;
  // ---
  private final GausDecayLookup gausP;
  private final GausDecayLookup gausN;

  /** @param interval [us] */
  public SAEGausDecayImage(DavisDevice davisDevice, int interval) {
    super(davisDevice, interval);
    // ---
    gausP = new GausDecayLookup(interval >> DROP, 2.5, +1);
    gausN = new GausDecayLookup(interval >> DROP, 2.5, -1);
  }

  @Override
  protected void assign(int delta, DavisDvsEvent dvsDavisEvent) {
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = dvsDavisEvent.brightToDark() ? gausN.get(delta >> DROP) : gausP.get(delta >> DROP);
  }
}
