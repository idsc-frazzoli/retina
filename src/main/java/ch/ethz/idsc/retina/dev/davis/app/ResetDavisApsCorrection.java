// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisApsEventListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisApsEvent;

public class ResetDavisApsCorrection extends DavisApsCorrection implements DavisApsEventListener {
  public ResetDavisApsCorrection() {
    super(new int[180 * 240]);
  }

  @Override
  public void aps(DavisApsEvent davisApsEvent) {
    // TODO use incremental method
    pitchblack[davisApsEvent.y + davisApsEvent.x * 180] = davisApsEvent.adc();
  }
}
