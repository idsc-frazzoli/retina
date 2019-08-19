// code by jph
package ch.ethz.idsc.retina.util.data;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class DelayedQueue<E> {
  private final Queue<E> queue = new LinkedList<>();
  private final int maxSize;

  /** @param maxSize */
  public DelayedQueue(int maxSize) {
    if (maxSize < 0)
      throw new RuntimeException("" + maxSize);
    this.maxSize = maxSize;
  }

  public Optional<E> push(E object) {
    queue.add(object);
    return maxSize < queue.size() //
        ? Optional.of(queue.poll())
        : Optional.empty();
  }
}
