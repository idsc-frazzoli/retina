// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

public class QueuedDvsEventSupplier implements DvsEventSupplier {
  private final Queue<DvsEvent> queue = new PriorityQueue<>();
  private final Dimension dimension;

  /** @param dimension
   * @param duration_us */
  public QueuedDvsEventSupplier(Collection<DvsEvent> collection, Dimension dimension) {
    queue.addAll(collection);
    this.dimension = dimension;
  }

  @Override
  public DvsEvent next() throws Exception {
    return queue.poll();
  }

  @Override
  public Dimension dimension() {
    return dimension;
  }
}
