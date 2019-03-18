// code by jph
package ch.ethz.idsc.retina.util.data;

import java.util.LinkedList;
import java.util.Optional;

public class DelayedQueue<E> {
  private final LinkedList<E> linkedList = new LinkedList<>();
  private final int maxSize;

  /** @param maxSize */
  public DelayedQueue(int maxSize) {
    this.maxSize = maxSize;
  }

  public Optional<E> push(E object) {
    linkedList.add(object);
    return maxSize < linkedList.size() //
        ? Optional.of(linkedList.removeFirst())
        : Optional.empty();
  }
}
