// code by jph
package ch.ethz.idsc.retina.dvs.digest;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

/** the buffer holds the recent history of events the history goes back for a
 * fixed window in time */
public class DvsEventBuffer implements DvsEventDigest {
  private final int window_us;
  private final Queue<DvsEvent> queue = new ArrayDeque<>();

  public DvsEventBuffer(int window_us) {
    this.window_us = window_us;
  }

  @Override
  public void digest(DvsEvent dvsEvent) {
    long limit = dvsEvent.time_us - window_us;
    while (!queue.isEmpty())
      if (queue.peek().time_us <= limit)
        queue.remove();
      else
        break;
    queue.add(dvsEvent);
  }

  public Collection<DvsEvent> collection() {
    return Collections.unmodifiableCollection(queue);
  }

  public int size() {
    return queue.size();
  }

  public int window_us() {
    return window_us;
  }
}
