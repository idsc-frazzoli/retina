// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** 2) in the absence of APS images, one period is */
public class DvsTallyProvider implements DavisDvsListener {
  private final DavisTallyListener davisTallyListener;
  private DavisTallyEvent davisTallyEvent;
  private int shift = 8;

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (Objects.isNull(davisTallyEvent))
      davisTallyEvent = new DavisTallyEvent(davisDvsEvent.time, shift);
    davisTallyEvent.register(davisDvsEvent.time);
    if (400 < davisTallyEvent.binLast) {
      davisTallyEvent.setMax(davisDvsEvent.time);
      davisTallyListener.tallyEvent(davisTallyEvent);
      int next = davisTallyEvent.first + (400 << shift); // TODO this is useless when working from a log file
      davisTallyEvent = new DavisTallyEvent(next, shift);
    }
  }

  public DvsTallyProvider(DavisTallyListener davisTallyListener) {
    this.davisTallyListener = davisTallyListener;
  }

  public void setShift(int shift) {
    this.shift = shift;
  }

  public int getShift() {
    return shift;
  }
}
