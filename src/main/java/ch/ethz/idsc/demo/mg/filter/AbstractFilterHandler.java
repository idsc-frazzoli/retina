// code by mg
package ch.ethz.idsc.demo.mg.filter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** base class for SLAM algorithm filtering. Filtered events are passed to all algorithm modules
 * in the listeners field of the class */
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

  /** calls stop() method of all elements in the listeners field that implement {@link StartAndStoppable} */
  public void stopStoppableListeners() {
    listeners.stream() //
        .filter(StartAndStoppable.class::isInstance) //
        .map(StartAndStoppable.class::cast) //
        .forEach(StartAndStoppable::stop);
  }
}
