// code by mg
package ch.ethz.idsc.demo.mg.filter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** only filtered events are passed to the listeners */
public abstract class AbstractFilterHandler implements DavisDvsListener, DavisDvsEventFilter {
  private final List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();

  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (filter(davisDvsEvent))
      listeners.forEach(listener -> listener.davisDvs(davisDvsEvent));
  }

  /** @param davisDvsListener */
  public final void addListener(DavisDvsListener davisDvsListener) {
    listeners.add(davisDvsListener);
  }
}
