// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** 2) in the absence of APS images, one period is */
public class DvsTallyProvider implements DavisDvsListener {
  public static final int BINS = 400;
  // ---
  private final DavisTallyListener davisTallyListener;
  private int shift = 8;
  private int window;
  private DavisTallyEvent davisTallyEvent = new DavisTallyEvent(0, shift);

  public DvsTallyProvider(DavisTallyListener davisTallyListener) {
    this.davisTallyListener = davisTallyListener;
    setShift(shift);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    int diff = davisDvsEvent.time - davisTallyEvent.first;
    if (0 <= diff && diff < window)
      davisTallyEvent.register(davisDvsEvent.time, davisDvsEvent.i);
    else //
    if (window <= diff && diff < 2 * window) {
      davisTallyEvent.setMax(davisTallyEvent.first + window);
      davisTallyListener.tallyEvent(davisTallyEvent);
      davisTallyEvent = new DavisTallyEvent(davisTallyEvent.first + window, shift);
      davisTallyEvent.register(davisDvsEvent.time, davisDvsEvent.i);
    } else
      davisTallyEvent = new DavisTallyEvent(davisDvsEvent.time, shift);
  }

  public void setShift(int shift) {
    this.shift = shift;
    window = BINS << shift;
  }

  public int getShift() {
    return shift;
  }
}
