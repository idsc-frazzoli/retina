// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisApsListener;
import ch.ethz.idsc.retina.davis._240c.DavisApsEvent;

/** collects complete image of latest reset aps */
public class ResetDavisApsCorrection implements DavisApsCorrection, DavisApsListener {
  /** alignment column 0, column 1, ... */
  private final int[] pitchblack;
  private int count = -1;

  public ResetDavisApsCorrection() {
    this.pitchblack = new int[240 * 180];
  }

  @Override
  public int nextReference() {
    return pitchblack[++count];
  }

  @Override
  public void reset() {
    count = -1;
  }

  @Override
  public void davisAps(DavisApsEvent davisApsEvent) {
    // TODO LT use incremental method
    pitchblack[davisApsEvent.y + davisApsEvent.x * 180] = davisApsEvent.adc();
  }
}
