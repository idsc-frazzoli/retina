// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.util.LinkedList;
import java.util.List;

public class ListenerQueue<T> {
  protected final List<T> listeners = new LinkedList<>();

  public void addListener(T listener) {
    listeners.add(listener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }
}
